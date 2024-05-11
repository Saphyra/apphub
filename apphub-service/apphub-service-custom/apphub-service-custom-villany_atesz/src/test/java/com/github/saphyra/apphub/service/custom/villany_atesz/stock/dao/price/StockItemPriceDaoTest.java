package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class StockItemPriceDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final String STOCK_ITEM_ID_STRING = "stock-item-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StockItemPriceConverter converter;

    @Mock
    private StockItemPriceRepository repository;

    @InjectMocks
    private StockItemPriceDao underTest;

    @Mock
    private StockItemPrice domain;

    @Mock
    private StockItemPriceEntity entity;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void deleteByStockItemId() {
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);

        underTest.deleteByStockItemId(STOCK_ITEM_ID);

        then(repository).should().deleteByStockItemId(STOCK_ITEM_ID_STRING);
    }

    @Test
    void getByStockItemId() {
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);
        given(repository.getByStockItemId(STOCK_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByStockItemId(STOCK_ITEM_ID)).containsExactly(domain);
    }
}