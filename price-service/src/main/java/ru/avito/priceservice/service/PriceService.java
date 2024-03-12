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
import ru.avito.priceservice.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
        List<DiscountSegment> segmentsByUser = segmentRepository.findByUser(request.userId());
        Storage currentStorage = storageService.getCurrentStorage();

        //сегменты не найдены изем из baseline матрицы
        if(segmentsByUser.isEmpty())  {
            Long priceByMatrix = matrixDao.findPriceByMatrix(currentStorage.baseline(), request.microCategoryId(), request.locationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Internal error"));
            //todo поиск наверх

            //todo получать id матрицы из кэша или чет такое, ибо будет накладно
            var matrixId = getMatrixId(currentStorage);
            return new ResponsePrice(priceByMatrix, request.locationId(), request.microCategoryId(), matrixId, null);
        } else {
//            Optional<Long> priceByMatrix = matrixDao.findPriceByDiscount();
//            if(priceByMatrix.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Internal error"); //цена всегда должна быть найдена


        }
        return new ResponsePrice(1L, 1L, 1L, 1L, 1L);
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
