package com.cfd.marketdata.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cfd.marketdata.api.MarketQuoteRequest;
import com.cfd.marketdata.service.MarketDataService;

@FeignClient(name = "cfd-market-data-service", path = "/market-data", url = "${services.market-data.url:http://localhost:8087}")
public interface MarketDataServiceFeignClient {

    @PostMapping("/quotes")
    MarketDataService.Quote updateQuote(@RequestBody MarketQuoteRequest request);

    @GetMapping("/quotes/{symbol}")
    MarketDataService.Quote latest(@PathVariable("symbol") String symbol);
}
