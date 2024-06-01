package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
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
class ResetInventoriedServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private StockItemDao stockItemDao;

    @InjectMocks
    private ResetInventoriedService underTest;

    @Mock
    private StockItem stockItem;

    @Test
    void resetInventoried() {
        given(stockItemDao.getByUserId(USER_ID)).willReturn(List.of(stockItem));

        underTest.resetInventoried(USER_ID);

        then(stockItem).should().setInventoried(false);
        then(stockItemDao).should().saveAll(List.of(stockItem));
    }
}