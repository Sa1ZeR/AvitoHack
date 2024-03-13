package ru.avito.priceservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.avito.priceservice.dao.MatrixDao;
import ru.avito.priceservice.dto.ResponsePrice;
import ru.avito.priceservice.dto.Storage;
import ru.avito.priceservice.entity.DiscountSegment;
import ru.avito.priceservice.entity.MapMatrix;
import ru.avito.priceservice.errors.PriceNotFoundError;
import ru.avito.priceservice.repository.CategoryRepository;
import ru.avito.priceservice.repository.DiscountSegmentRepository;
import ru.avito.priceservice.repository.LocationRepository;
import ru.avito.priceservice.repository.MapMatrixRepository;

import java.util.Comparator;
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
    public ResponsePrice calcPrice(Long locationId, Long microCategoryId, Long userId) {
        var segments = segmentRepository.findByUser(userId).stream()
                .map(DiscountSegment::getSegment)
                .sorted(Comparator.reverseOrder())
                .toList();
        var currentStorage = storageService.getCurrentStorage();

        //если сегменты есть, то надо проверить есть ли такие сегменты в сторэдже
        var discountSegments = segments.stream()
                .filter(segment -> currentStorage.discounts().containsKey(segment))
                .toList();

        //Если сегментов нет или таких сегментов нет в сторэдже, то ищем по baseline матрице
        if(segments.isEmpty() || discountSegments.isEmpty())  {
            var result = getPriceByMatrix(locationId, microCategoryId, currentStorage.baseline())
                    .orElseThrow(PriceNotFoundError::new);
            var matrixId = getMatrixId(currentStorage);
            return new ResponsePrice(result.price(), result.locationId(), result.microCategoryId(), matrixId, null);
        }

        //сперва ищем цену в скидочных матрицах
        Long userSegment = null;
        Result result = null;
        Optional<Result> discountOpt = Optional.empty();
        for (var segment : discountSegments) {
            discountOpt = getPriceByMatrix(locationId, microCategoryId, currentStorage.discounts().get(segment));
            if (discountOpt.isPresent()) {
                result = discountOpt.get();
                userSegment = segment;
                break;
            }
        }

        //если цены в скидочных матрицах нет, то переходим в основную матрицу
        if (discountOpt.isEmpty()) {
            result = getPriceByMatrix(locationId, microCategoryId, currentStorage.baseline())
                    .orElseThrow(PriceNotFoundError::new);
        }

        var matrixId = getMatrixId(currentStorage);
        return new ResponsePrice(result.price(), result.locationId(), result.microCategoryId(), matrixId, userSegment);
    }

    private Optional<Result> getPriceByMatrix(Long locationId, Long microCategoryId, String matrixTableName) {
        long startCategory = microCategoryId;
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
                            .orElse(null);
                    if (locationId == null) {
                        return Optional.empty();
                    }
                    microCategoryId = startCategory;
                }
            }
        } while (priceByMatrix.isEmpty());
        return Optional.of(new Result(priceByMatrix.get(), locationId, microCategoryId));
    }

    private record Result(Long price, Long locationId, Long microCategoryId) {}

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
