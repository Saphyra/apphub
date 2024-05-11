package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category.StockCategoryDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item.DeleteStockItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteStockCategoryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();

    @Mock
    private StockCategoryDao stockCategoryDao;

    @Mock
    private DeleteStockItemService deleteStockItemService;

    @Mock
    private StockCategoryModelCache stockCategoryModelCache;

    @InjectMocks
    private DeleteStockCategoryService underTest;

    @Test
    void delete() {
        underTest.delete(USER_ID, STOCK_CATEGORY_ID);

        then(stockCategoryDao).should().deleteByUserIdAndStockCategoryId(USER_ID, STOCK_CATEGORY_ID);
        then(deleteStockItemService).should().deleteByStockCategoryId(STOCK_CATEGORY_ID);
        then(stockCategoryModelCache).should().invalidate(STOCK_CATEGORY_ID);
    }
}