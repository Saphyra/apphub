package com.github.saphyra.apphub.service.custom.elite_base.util;

import com.github.saphyra.apphub.api.feature.elite_base.model.Order;
import com.github.saphyra.apphub.lib.sql_builder.keyword.OrderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ConversionUtilsTest {
    @Test
    void toOrderType() {
        assertThat(ConversionUtils.toOrderType(Order.ASCENDING)).isEqualTo(OrderType.ASC);
        assertThat(ConversionUtils.toOrderType(Order.DESCENDING)).isEqualTo(OrderType.DESC);
    }
}