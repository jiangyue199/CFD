package com.cfd.marketdata.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.marketdata.persistence.MarketQuoteDbMapper;
import com.cfd.marketdata.persistence.MarketQuoteEntity;

@Service
public class MarketDataService {

    private final MarketQuoteDbMapper marketQuoteDbMapper;

    public MarketDataService(MarketQuoteDbMapper marketQuoteDbMapper) {
        this.marketQuoteDbMapper = marketQuoteDbMapper;
    }

    public Quote update(String symbol, BigDecimal bid, BigDecimal ask) {
        Instant now = Instant.now();
        MarketQuoteEntity existing = marketQuoteDbMapper.selectOne(new LambdaQueryWrapper<MarketQuoteEntity>()
                .eq(MarketQuoteEntity::getSymbol, symbol)
                .last("LIMIT 1"));
        if (existing == null) {
            existing = new MarketQuoteEntity();
            existing.setSymbol(symbol);
            existing.setBid(bid);
            existing.setAsk(ask);
            existing.setQuoteTime(now);
            marketQuoteDbMapper.insert(existing);
        } else {
            existing.setBid(bid);
            existing.setAsk(ask);
            existing.setQuoteTime(now);
            marketQuoteDbMapper.updateById(existing);
        }
        return toQuote(existing);
    }

    public Quote latest(String symbol) {
        MarketQuoteEntity quote = marketQuoteDbMapper.selectOne(new LambdaQueryWrapper<MarketQuoteEntity>()
                .eq(MarketQuoteEntity::getSymbol, symbol)
                .last("LIMIT 1"));
        if (quote == null) {
            quote = new MarketQuoteEntity();
            quote.setSymbol(symbol);
            quote.setBid(BigDecimal.ZERO);
            quote.setAsk(BigDecimal.ZERO);
            quote.setQuoteTime(Instant.now());
            marketQuoteDbMapper.insert(quote);
        }
        return toQuote(quote);
    }

    private Quote toQuote(MarketQuoteEntity entity) {
        return new Quote(entity.getSymbol(), entity.getBid(), entity.getAsk(), entity.getQuoteTime());
    }

    public record Quote(String symbol, BigDecimal bid, BigDecimal ask, Instant timestamp) {}
}
