package ru.avito.priceservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.avito.priceservice.annotation.PostInitCache;
import ru.avito.priceservice.cache.Cache;
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

import java.util.*;
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
    private final Cache cache;
    private Map<String, Long> matrixIdCache;

    @PostInitCache
    public void initCache() {
        var matrices = mapMatrixRepository.findAll();
        matrixIdCache = matrices.stream()
                .collect(Collectors.toMap(MapMatrix::getName, MapMatrix::getId));
        var storage = storageService.getCurrentStorage();
        var discountsMatrix = storage.discounts().entrySet();
        for (var entry : discountsMatrix) {
            var categoryIds = matrixDao.findDistinctCategoryIds(entry.getValue());
            var locationIds = matrixDao.findDistinctLocationIds(entry.getValue());
            if (categoryIds.size() == locationIds.size()) {
                cache.addIdsForCategories(entry.getKey(), categoryIds);
                cache.addIdsForLocations(entry.getKey(), locationIds);
            } else if (categoryIds.size() < locationIds.size()) {
                cache.addIdsForCategories(entry.getKey(), categoryIds);
            } else {
                cache.addIdsForLocations(entry.getKey(), locationIds);
            }
        }
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
        var categoryParent = getCategoryTree(microCategoryId);
        var locationParent = getLocationTree(locationId);
        for (var segment : discountSegments) {
            var matrixTableName = currentStorage.discounts().get(segment);

            var resultIds = getMatrixResultIds(locationId, microCategoryId, segment, categoryParent, locationParent);

            if (resultIds.categoryParentIds().isEmpty() || resultIds.locationParentIds().isEmpty()) {
                continue;
            }

            microCategoryId = resultIds.categoryParentIds().getFirst();
            locationId = resultIds.locationParentIds().getFirst();

            discountOpt = getPriceByMatrix(locationId, microCategoryId, matrixTableName);
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

    private MatrixResultIds getMatrixResultIds(Long locationId, Long microCategoryId, Long segment, List<Long> categoryParent, List<Long> locationParent) {
        var categoryParentIds = new ArrayList<>(categoryParent);
        var locationParentIds = new ArrayList<>(locationParent);

        var categoryIds = cache.categoryIds(segment);
        var locationsIds = cache.locationsIds(segment);

        if (locationsIds == null) {
            var contains = categoryIds.contains(microCategoryId);
            if (!contains) {
                categoryParentIds.removeIf(el -> !categoryIds.contains(el));
            }
        } else if (categoryIds == null) {
            var contains = locationsIds.contains(locationId);
            if (!contains) {
                locationParentIds.removeIf(el -> !locationsIds.contains(el));
            }
        } else {
            locationParentIds.removeIf(el -> !locationsIds.contains(el));
            categoryParentIds.removeIf(el -> !categoryIds.contains(el));
        }
        return new MatrixResultIds(categoryParentIds, locationParentIds);
    }

    private record MatrixResultIds(ArrayList<Long> categoryParentIds, ArrayList<Long> locationParentIds) {
    }

    private List<Long> getCategoryTree(Long startId) {
        List<Long> categoryParentIds = new ArrayList<>();
        categoryParentIds.add(startId);
        categoryParentIds.addAll(categoryRepository.findAllParentIdsById(startId));
        return categoryParentIds;
    }

    private List<Long> getLocationTree(Long startId) {
        List<Long> categoryParentIds = new ArrayList<>();
        categoryParentIds.add(startId);
        categoryParentIds.addAll(locationRepository.findAllParentIdsById(startId));
        return categoryParentIds;
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
