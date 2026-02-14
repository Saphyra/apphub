package com.github.saphyra.apphub.service.custom.elite_base.util;

import com.github.saphyra.apphub.api.custom.elite_base.model.Order;
import com.github.saphyra.apphub.lib.sql_builder.OrderType;

public class ConversionUtils {
    public static OrderType toOrderType(Order order) {
        return switch (order) {
            case ASCENDING -> OrderType.ASC;
            case DESCENDING -> OrderType.DESC;
        };
    }
}
