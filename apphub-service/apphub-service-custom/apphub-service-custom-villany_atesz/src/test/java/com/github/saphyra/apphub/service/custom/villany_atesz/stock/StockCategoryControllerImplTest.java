package com.github.saphyra.apphub.service.custom.villany_atesz.stock;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockCategoryModel;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.CreateStockCategoryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.DeleteStockCategoryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.EditStockCategoryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.StockCategoryQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class StockCategoryControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();

    @Mock
    private CreateStockCategoryService createStockCategoryService;

    @Mock
    private EditStockCategoryService editStockCategoryService;

    @Mock
    private DeleteStockCategoryService deleteStockCategoryService;

    @Mock
    private StockCategoryQueryService stockCategoryQueryService;

    @InjectMocks
    private StockCategoryControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private StockCategoryModel model;

    @BeforeEach
    void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    void getStockCategories() {
        given(stockCategoryQueryService.getStockCategories(USER_ID)).willReturn(List.of(model));

        assertThat(underTest.getStockCategories(accessTokenHeader)).containsExactly(model);
    }

    @Test
    void createStockCategory() {
        given(stockCategoryQueryService.getStockCategories(USER_ID)).willReturn(List.of(model));

        assertThat(underTest.createStockCategory(model, accessTokenHeader)).containsExactly(model);

        then(createStockCategoryService).should().create(USER_ID, model);
    }

    @Test
    void editStockCategory() {
        given(stockCategoryQueryService.getStockCategories(USER_ID)).willReturn(List.of(model));

        assertThat(underTest.editStockCategory(model, STOCK_CATEGORY_ID, accessTokenHeader)).containsExactly(model);

        then(editStockCategoryService).should().edit(STOCK_CATEGORY_ID, model);
    }

    @Test
    void deleteStockCategory() {
        given(stockCategoryQueryService.getStockCategories(USER_ID)).willReturn(List.of(model));

        assertThat(underTest.deleteStockCategory(STOCK_CATEGORY_ID, accessTokenHeader)).containsExactly(model);

        then(deleteStockCategoryService).should().delete(USER_ID, STOCK_CATEGORY_ID);
    }
}