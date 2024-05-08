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
class CreateStockCategoryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private StockCategoryValidator stockCategoryValidator;

    @Mock
    private StockCategoryFactory stockCategoryFactory;

    @Mock
    private StockCategoryDao stockCategoryDao;

    @InjectMocks
    private CreateStockCategoryService underTest;

    @Mock
    private StockCategory stockCategory;

    @Mock
    private StockCategoryModel request;

    @Test
    void create() {
        given(stockCategoryFactory.create(USER_ID, request)).willReturn(stockCategory);

        underTest.create(USER_ID, request);

        then(stockCategoryValidator).should().validate(request);
        then(stockCategoryDao).should().save(stockCategory);
    }
}