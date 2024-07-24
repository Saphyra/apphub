package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.acquisition.AcquisitionDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeleteStockItemServiceTest {
    private static final UUID STOCK_CATEGORY_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private StockItemDao stockItemDao;

    @Mock
    private StockItemPriceDao stockItemPriceDao;

    @Mock
    private CartItemDao cartItemDao;

    @Mock
    private AcquisitionDao acquisitionDao;

    @InjectMocks
    private DeleteStockItemService underTest;

    @Mock
    private StockItem stockItem;

    @Test
    void deleteByStockCategoryId() {
        given(stockItemDao.getByStockCategoryId(STOCK_CATEGORY_ID)).willReturn(List.of(stockItem));
        given(stockItem.getStockItemId()).willReturn(STOCK_ITEM_ID);
        given(stockItem.getUserId()).willReturn(USER_ID);

        underTest.deleteByStockCategoryId(STOCK_CATEGORY_ID);

        then(stockItemDao).should().deleteByUserIdAndStockItemId(USER_ID, STOCK_ITEM_ID);
        then(stockItemPriceDao).should().deleteByStockItemId(STOCK_ITEM_ID);
        then(cartItemDao).should().deleteByStockItemId(STOCK_ITEM_ID);
        then(acquisitionDao).should().deleteByStockItemIdAndUserId(STOCK_ITEM_ID, USER_ID);
    }
}