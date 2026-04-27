package com.cfd.marketdata.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.marketdata.service.MarketDataService;

@RestController
@RequestMapping("/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @PostMapping("/quotes")
    public MarketDataService.Quote update(@Validated @RequestBody MarketQuoteRequest request) {
        return marketDataService.update(request.symbol(), request.bid(), request.ask());
    }

    @GetMapping("/quotes/{symbol}")
    public MarketDataService.Quote latest(@PathVariable String symbol) {
        return marketDataService.latest(symbol);
    }
}
