package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class StockCategoryModelConverterTest {
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String MEASUREMENT = "measurement";

    private final StockCategoryModelConverter underTest = new StockCategoryModelConverter();

    @Test
    void convert() {
        StockCategory stockCategory = StockCategory.builder()
            .stockCategoryId(STOCK_CATEGORY_ID)
            .name(NAME)
            .measurement(MEASUREMENT)
            .build();

        assertThat(underTest.convert(stockCategory))
            .returns(STOCK_CATEGORY_ID, StockCategoryModel::getStockCategoryId)
            .returns(NAME, StockCategoryModel::getName)
            .returns(MEASUREMENT, StockCategoryModel::getMeasurement);
    }
}