package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockCategoryValidatorTest {
    private static final String MEASUREMENT = "measurement";
    private static final String NAME = "name";

    private final StockCategoryValidator underTest = new StockCategoryValidator();

    @Test
    void blankName() {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(" ")
            .measurement(MEASUREMENT)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "name", "must not be null or blank");
    }

    @Test
    void nullMeasurement() {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(NAME)
            .measurement(null)
            .build();

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "measurement", "must not be null");
    }

    @Test
    void valid() {
        StockCategoryModel request = StockCategoryModel.builder()
            .name(NAME)
            .measurement(MEASUREMENT)
            .build();

        underTest.validate(request);
    }
}