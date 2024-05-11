package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceConverter.COLUMN_PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StockItemPriceConverterTest {
    private static final UUID STOCK_ITEM_PRICE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer PRICE = 64;
    private static final String USER_ID_IN_ACCESS_TOKEN = "user-id-in-access-token";
    private static final String STOCK_ITEM_PRICE_ID_STRING = "stock-item-price-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String STOCK_ITEM_ID_STRING = "stock-item-id";
    private static final String ENCRYPTED_PRICE = "encrypted-price";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private StockItemPriceConverter underTest;

    @Test
    void convertDomain() {
        StockItemPrice domain = StockItemPrice.builder()
            .stockItemPriceId(STOCK_ITEM_PRICE_ID)
            .userId(USER_ID)
            .stockItemId(STOCK_ITEM_ID)
            .price(PRICE)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_IN_ACCESS_TOKEN);
        given(uuidConverter.convertDomain(STOCK_ITEM_PRICE_ID)).willReturn(STOCK_ITEM_PRICE_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);
        given(integerEncryptor.encrypt(PRICE, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_PRICE_ID_STRING, COLUMN_PRICE)).willReturn(ENCRYPTED_PRICE);

        assertThat(underTest.convertDomain(domain))
            .returns(STOCK_ITEM_PRICE_ID_STRING, StockItemPriceEntity::getStockItemPriceId)
            .returns(USER_ID_STRING, StockItemPriceEntity::getUserId)
            .returns(STOCK_ITEM_ID_STRING, StockItemPriceEntity::getStockItemId)
            .returns(ENCRYPTED_PRICE, StockItemPriceEntity::getPrice);
    }

    @Test
    void convertEntity() {
        StockItemPriceEntity domain = StockItemPriceEntity.builder()
            .stockItemPriceId(STOCK_ITEM_PRICE_ID_STRING)
            .userId(USER_ID_STRING)
            .stockItemId(STOCK_ITEM_ID_STRING)
            .price(ENCRYPTED_PRICE)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_IN_ACCESS_TOKEN);
        given(uuidConverter.convertEntity(STOCK_ITEM_PRICE_ID_STRING)).willReturn(STOCK_ITEM_PRICE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(STOCK_ITEM_ID_STRING)).willReturn(STOCK_ITEM_ID);
        given(integerEncryptor.decrypt(ENCRYPTED_PRICE, USER_ID_IN_ACCESS_TOKEN, STOCK_ITEM_PRICE_ID_STRING, COLUMN_PRICE)).willReturn(PRICE);

        assertThat(underTest.convertEntity(domain))
            .returns(STOCK_ITEM_PRICE_ID, StockItemPrice::getStockItemPriceId)
            .returns(USER_ID, StockItemPrice::getUserId)
            .returns(STOCK_ITEM_ID, StockItemPrice::getStockItemId)
            .returns(PRICE, StockItemPrice::getPrice);
    }
}