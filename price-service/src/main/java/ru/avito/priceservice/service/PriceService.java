package ru.avito.priceservice.service;

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
import ru.avito.priceservice.repository.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final MatrixDao matrixDao;
    private final MapMatrixRepository mapMatrixRepository;
    private final StorageService storageService;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final DiscountSegmentRepository segmentRepository;

    @Transactional
    public ResponsePrice calcPrice(RequestPrice request) {
        List<DiscountSegment> segmentsByUser = segmentRepository.findByUser(request.userId());
        Storage currentStorage = storageService.getCurrentStorage();

        //сегменты не найдены изем из baseline матрицы
        if(segmentsByUser.size() == 0)  {
            Optional<Long> priceByMatrix = matrixDao.findPriceByMatrix(currentStorage.baseline(), request.microCategoryId(), request.locationId());
            //todo поиск наверх
            if(priceByMatrix.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Internal error"); //цена всегда должна быть найдена

            //todo получать id матрицы из кэша или чет такое, ибо будет накладно
            return new ResponsePrice(priceByMatrix.get(), request.locationId(), request.microCategoryId(), mapMatrixRepository.findByName(currentStorage.baseline()).get().getId(), null);
        } else {
//            Optional<Long> priceByMatrix = matrixDao.findPriceByDiscount();
//            if(priceByMatrix.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Internal error"); //цена всегда должна быть найдена


        }
        return new ResponsePrice(1L, 1L, 1L, 1L, 1L);
    }
}
