package ru.avito.priceservice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.avito.priceservice.dto.RequestPrice;
import ru.avito.priceservice.dto.ResponsePrice;
import ru.avito.priceservice.service.PriceService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/price")
public class PriceController {

    private final PriceService priceService;

    /**
     * Пример запроса когда пользователь попадает в скидку
     * <pre class="code">
     * {
     *     "location_id": 7,
     *     "microcategory_id": 5,
     *     "user_id": 1
     * }
     * </pre>
     * Пример запроса когда пользователь попадает в основную матрицу
     * <pre class="code">
     *{
     *     "location_id": 7,
     *     "microcategory_id": 5,
     *     "user_id": 4
     * }
     * </pre>
     * @param request {@link  RequestPrice}
     * @return status OK with {@link  ResponsePrice} json
     */
    @GetMapping
    public ResponsePrice price(@RequestBody RequestPrice request) {
        return priceService.calcPrice(request);
    }
}
