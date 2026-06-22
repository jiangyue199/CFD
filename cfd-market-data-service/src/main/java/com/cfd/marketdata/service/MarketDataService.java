package com.cfd.marketdata.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cfd.marketdata.persistence.MarketQuoteDbMapper;
import com.cfd.marketdata.persistence.MarketQuoteEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 行情数据服务。
 *
 * <p>封装市场报价的业务逻辑，包括报价的更新（新增或覆盖）和最新报价查询。</p>
 *
 * @author CFD Platform Team
 */
@Service
public class MarketDataService {

    private final MarketQuoteDbMapper marketQuoteDbMapper;

    /**
     * 构造行情数据服务。
     *
     * @param marketQuoteDbMapper 市场报价数据库 Mapper
     */
    public MarketDataService(MarketQuoteDbMapper marketQuoteDbMapper) {
        this.marketQuoteDbMapper = marketQuoteDbMapper;
    }

    /**
     * 更新指定交易品种的报价。
     *
     * <p>若该品种不存在则新增记录，否则更新买入价和卖出价。</p>
     *
     * @param symbol 交易品种代码
     * @param bid    买入价
     * @param ask    卖出价
     * @return 更新后的报价信息
     */
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

    /**
     * 获取指定交易品种的最新报价。
     *
     * <p>若该品种不存在，则自动创建零报价记录并返回。</p>
     *
     * @param symbol 交易品种代码
     * @return 最新报价信息
     */
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

    /**
     * 将数据库实体转换为报价记录。
     *
     * @param entity 市场报价数据库实体
     * @return 报价记录
     */
    private Quote toQuote(MarketQuoteEntity entity) {
        return new Quote(entity.getSymbol(), entity.getBid(), entity.getAsk(), entity.getQuoteTime());
    }

    /**
     * 市场报价记录。
     *
     * @param symbol    交易品种代码
     * @param bid       买入价
     * @param ask       卖出价
     * @param timestamp 报价时间戳
     */
    public record Quote(String symbol, BigDecimal bid, BigDecimal ask, Instant timestamp) {}
}
