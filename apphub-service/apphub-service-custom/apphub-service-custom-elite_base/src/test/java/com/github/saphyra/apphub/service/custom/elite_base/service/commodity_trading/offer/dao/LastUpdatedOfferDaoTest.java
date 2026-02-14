package com.github.saphyra.apphub.service.custom.elite_base.service.commodity_trading.offer.dao;

import com.github.saphyra.apphub.api.custom.elite_base.model.commodity_trading.OrderCommoditiesBy;
import com.github.saphyra.apphub.lib.sql_builder.QualifiedColumn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.COLUMN_LAST_UPDATE;
import static com.github.saphyra.apphub.service.custom.elite_base.common.DatabaseConstants.TABLE_LAST_UPDATE;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LastUpdatedOfferDaoTest {
    @Mock
    private OfferDaoContext context;

    @InjectMocks
    private LastUpdatedOfferDao underTest;

    @Test
    void getOrderByColumn() {
        assertThat(underTest.getOrderByColumn(null))
            .returns(TABLE_LAST_UPDATE, QualifiedColumn::getTable)
            .returns(COLUMN_LAST_UPDATE, QualifiedColumn::getColumn);
    }

    @Test
    void getOrderBy() {
        assertThat(underTest.getOrderBy()).isEqualTo(OrderCommoditiesBy.LAST_UPDATED);
    }
}