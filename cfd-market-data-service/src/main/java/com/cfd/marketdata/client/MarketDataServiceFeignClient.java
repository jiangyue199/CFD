package com.cfd.marketdata.client;

import com.cfd.marketdata.api.MarketQuoteRequest;
import com.cfd.marketdata.service.MarketDataService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 行情服务 Feign 客户端接口。
 *
 * <p>供其他微服务通过 Feign 远程调用行情服务的报价更新和查询接口。</p>
 *
 * @author CFD Platform Team
 */
@FeignClient(name = "cfd-market-data-service", path = "/market-data", url = "${services.market-data.url:http://localhost:8087}")
public interface MarketDataServiceFeignClient {

    /**
     * 远程调用报价更新接口。
     *
     * @param request 报价更新请求
     * @return 更新后的报价信息
     */
    @PostMapping("/quotes")
    MarketDataService.Quote updateQuote(@RequestBody MarketQuoteRequest request);

    /**
     * 远程获取指定交易品种的最新报价。
     *
     * @param symbol 交易品种代码
     * @return 最新报价信息
     */
    @GetMapping("/quotes/{symbol}")
    MarketDataService.Quote latest(@PathVariable("symbol") String symbol);
}
