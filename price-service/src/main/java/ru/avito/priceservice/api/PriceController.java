package ru.avito.priceservice.api;

import lombok.AllArgsConstructor;
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

    @GetMapping
    public ResponsePrice price(@RequestBody RequestPrice request) {
        return priceService.calcPrice(request);
    }
}
