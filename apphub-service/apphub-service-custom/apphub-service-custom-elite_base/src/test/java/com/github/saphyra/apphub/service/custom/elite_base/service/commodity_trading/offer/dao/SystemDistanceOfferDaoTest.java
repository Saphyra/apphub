package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.lib.sql_builder.Column;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_DISTANCE_FROM_REFERENCE;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SystemDistanceOfferDaoTest {
    @Mock
    private OfferDaoContext context;

    @InjectMocks
    private SystemDistanceOfferDao underTest;

    @Test
    void getOrderByColumn() {
        assertThat(underTest.getOrderByColumn(null))
            .returns(COLUMN_DISTANCE_FROM_REFERENCE, Column::get);
    }

    @Test
    void getOrderBy() {
        assertThat(underTest.getOrderBy()).isEqualTo(OrderCommoditiesBy.SYSTEM_DISTANCE);
    }
}