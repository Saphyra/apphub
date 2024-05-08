package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategory;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockCategoryQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();

    @Mock
    private StockCategoryDao stockCategoryDao;

    @Mock
    private StockCategoryModelConverter stockCategoryModelConverter;

    @Mock
    private StockCategoryModelCache stockCategoryModelCache;

    @InjectMocks
    private StockCategoryQueryService underTest;

    @Mock
    private StockCategory stockCategory;

    @Mock
    private StockCategoryModel model;

    @Test
    void getStockCategories() {
        given(stockCategoryDao.getByUserId(USER_ID)).willReturn(List.of(stockCategory));
        given(stockCategoryModelConverter.convert(stockCategory)).willReturn(model);

        assertThat(underTest.getStockCategories(USER_ID)).containsExactly(model);
    }

    @Test
    void findByStockCategoryId() {
        given(stockCategoryModelCache.load(STOCK_CATEGORY_ID)).willReturn(Optional.of(model));

        assertThat(underTest.findByStockCategoryId(STOCK_CATEGORY_ID)).isEqualTo(model);
    }

    @Test
    void findByStockCategoryId_notFound() {
        given(stockCategoryModelCache.load(STOCK_CATEGORY_ID)).willReturn(Optional.empty());

        ExceptionValidator.validateLoggedException(() -> underTest.findByStockCategoryId(STOCK_CATEGORY_ID), HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }
}