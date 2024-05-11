package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockCategoryFactoryTest {
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String MEASUREMENT = "measurement";

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private StockCategoryFactory underTest;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(STOCK_CATEGORY_ID);

        StockCategoryModel request = StockCategoryModel.builder()
            .name(NAME)
            .measurement(MEASUREMENT)
            .build();
        assertThat(underTest.create(USER_ID, request))
            .returns(STOCK_CATEGORY_ID, StockCategory::getStockCategoryId)
            .returns(USER_ID, StockCategory::getUserId)
            .returns(NAME, StockCategory::getName)
            .returns(MEASUREMENT, StockCategory::getMeasurement);
    }
}