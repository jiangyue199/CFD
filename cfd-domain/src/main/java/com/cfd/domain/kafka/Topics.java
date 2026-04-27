package com.cfd.domain.kafka;

public final class Topics {

    public static final String ORDER_OPEN_COMMAND = "cfd.order.open.command";
    public static final String TRADE_OPENED_EVENT = "cfd.trade.opened.event";
    public static final String TRADE_OPENED_FEEDBACK = "cfd.trade.opened.feedback";

    private Topics() {
    }
}
