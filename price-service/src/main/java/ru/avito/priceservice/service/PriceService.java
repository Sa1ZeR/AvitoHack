package ru.avito.priceservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.avito.priceservice.dao.MatrixDao;
import ru.avito.priceservice.dto.RequestPrice;
import ru.avito.priceservice.dto.ResponsePrice;
import ru.avito.priceservice.dto.Storage;
import ru.avito.priceservice.entity.DiscountSegment;
import ru.avito.priceservice.entity.MapMatrix;
import ru.avito.priceservice.errors.LocationNotFoundError;
import ru.avito.priceservice.repository.CategoryRepository;
import ru.avito.priceservice.repository.DiscountSegmentRepository;
import ru.avito.priceservice.repository.LocationRepository;
import ru.avito.priceservice.repository.MapMatrixRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final MatrixDao matrixDao;
    private final MapMatrixRepository mapMatrixRepository;
    private final StorageService storageService;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final DiscountSegmentRepository segmentRepository;
    private Map<String, Long> matrixIdCache;

    @PostConstruct
    public void init() {
        var matrices = mapMatrixRepository.findAll();
        matrixIdCache = matrices.stream()
                .collect(Collectors.toMap(MapMatrix::getName, MapMatrix::getId));
    }

    @Transactional(readOnly = true)
    public ResponsePrice calcPrice(RequestPrice request) {
        var segments = segmentRepository.findByUser(request.userId()).stream()
                .map(DiscountSegment::getSegment)
                .sorted(Comparator.reverseOrder())
                .toList();
        Storage currentStorage = storageService.getCurrentStorage();

        //TODO если сегменты есть, то надо проверить есть ли такие сегменты в сторэдже
        Long hasSegmentInDiscountStorage = null;
        for (var segment : segments) {
            if (currentStorage.discounts().containsKey(segment)) {
                hasSegmentInDiscountStorage = segment;
                break;
            }
        }
        //Если сегментов нет или таких сегментов нет в сторэдже, то ищем по baseline матрице
        var microCategoryId = request.microCategoryId();
        var locationId = request.locationId();
        if(segments.isEmpty() || hasSegmentInDiscountStorage == null)  {
            var price = getPriceByMatrix(request, currentStorage.baseline(), microCategoryId, locationId);
            var matrixId = getMatrixId(currentStorage);
            return new ResponsePrice(price, request.locationId(), request.microCategoryId(), matrixId, null);
        }
        var discountMatrix = currentStorage.discounts().get(hasSegmentInDiscountStorage);
        var price = getPriceByMatrix(request, discountMatrix, microCategoryId, locationId);
        var matrixId = getMatrixId(currentStorage);
        return new ResponsePrice(price, request.locationId(), request.microCategoryId(), matrixId, hasSegmentInDiscountStorage);
    }

    private Long getPriceByMatrix(RequestPrice request, String matrixTableName, Long microCategoryId, Long locationId) {
        Optional<Long> priceByMatrix;
        do {
            priceByMatrix = matrixDao.findPriceByMatrix(
                    matrixTableName,
                    microCategoryId,
                    locationId
            );
            if (priceByMatrix.isEmpty()) {
                var parentId = categoryRepository.findParentIdById(microCategoryId)
                        .orElse(null);
                if (parentId != null) {
                    microCategoryId = parentId;
                } else {
                    locationId = locationRepository.findParentIdById(locationId)
                            .orElseThrow(LocationNotFoundError::new);
                    microCategoryId = request.microCategoryId();
                }
            }
        } while (priceByMatrix.isEmpty());
        return priceByMatrix.get();
    }

    private Long getMatrixId(Storage currentStorage) {
        Long matrixId;
        if (matrixIdCache.containsKey(currentStorage.baseline())) {
            matrixId = matrixIdCache.get(currentStorage.baseline());
        } else {
            matrixId = mapMatrixRepository.findByName(currentStorage.baseline())
                    .map(MapMatrix::getId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "matrix not found"));
        }
        return matrixId;
    }
}
