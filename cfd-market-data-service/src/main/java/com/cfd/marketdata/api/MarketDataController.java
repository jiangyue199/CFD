package com.cfd.marketdata.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cfd.marketdata.service.MarketDataService;

/**
 * 行情数据 REST 控制器。
 *
 * <p>提供市场报价的更新和查询 HTTP 接口，
 * 所有请求路径以 {@code /market-data} 为前缀。</p>
 *
 * @author CFD Platform Team
 */
@RestController
@RequestMapping("/market-data")
public class MarketDataController {

    private final MarketDataService marketDataService;

    /**
     * 构造行情数据控制器。
     *
     * @param marketDataService 行情数据服务
     */
    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    /**
     * 更新市场报价。
     *
     * @param request 包含交易品种、买入价和卖出价的报价请求
     * @return 更新后的报价信息
     */
    @PostMapping("/quotes")
    public MarketDataService.Quote update(@Validated @RequestBody MarketQuoteRequest request) {
        return marketDataService.update(request.symbol(), request.bid(), request.ask());
    }

    /**
     * 获取指定交易品种的最新报价。
     *
     * @param symbol 交易品种代码
     * @return 最新报价信息
     */
    @GetMapping("/quotes/{symbol}")
    public MarketDataService.Quote latest(@PathVariable String symbol) {
        return marketDataService.latest(symbol);
    }
}
