package com.cfd.marketdata.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class MarketDataService {

    private final Map<String, Quote> latestQuotes = new ConcurrentHashMap<>();

    public Quote update(String symbol, BigDecimal bid, BigDecimal ask) {
        Quote quote = new Quote(symbol, bid, ask, Instant.now());
        latestQuotes.put(symbol, quote);
        return quote;
    }

    public Quote latest(String symbol) {
        return latestQuotes.computeIfAbsent(symbol, key -> new Quote(key, BigDecimal.ZERO, BigDecimal.ZERO, Instant.now()));
    }

    public record Quote(String symbol, BigDecimal bid, BigDecimal ask, Instant timestamp) {}
}
