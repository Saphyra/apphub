package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategory;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EditStockCategoryServiceTest {
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String MEASUREMENT = "measurement";

    @Mock
    private StockCategoryValidator stockCategoryValidator;

    @Mock
    private StockCategoryDao stockCategoryDao;

    @Mock
    private StockCategoryModelCache stockCategoryModelCache;

    @InjectMocks
    private EditStockCategoryService underTest;

    @Mock
    private StockCategoryModel request;

    @Mock
    private StockCategory stockCategory;

    @Test
    void edit() {
        given(stockCategoryDao.findByIdValidated(STOCK_CATEGORY_ID)).willReturn(stockCategory);
        given(request.getName()).willReturn(NAME);
        given(request.getMeasurement()).willReturn(MEASUREMENT);

        underTest.edit(STOCK_CATEGORY_ID, request);

        then(stockCategoryValidator).should().validate(request);
        then(stockCategory).should().setName(NAME);
        then(stockCategory).should().setMeasurement(MEASUREMENT);
        then(stockCategoryDao).should().save(stockCategory);
        then(stockCategoryModelCache).should().invalidate(STOCK_CATEGORY_ID);
    }
}