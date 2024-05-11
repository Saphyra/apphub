package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemConverter.COLUMN_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CartItemConverterTest {
    private static final UUID CART_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final Integer AMOUNT = 32;
    private static final String CART_ITEM_ID_STRING = "cart-item-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String CART_ID_STRING = "cart-id";
    private static final String STOCK_ITEM_ID_STRING = "stock-item-id";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_AMOUNT = "encrypted-amount";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @InjectMocks
    private CartItemConverter underTest;

    @Test
    void convertDomain() {
        CartItem domain = CartItem.builder()
            .cartItemId(CART_ITEM_ID)
            .userId(USER_ID)
            .cartId(CART_ID)
            .stockItemId(STOCK_ITEM_ID)
            .amount(AMOUNT)
            .build();

        given(uuidConverter.convertDomain(CART_ITEM_ID)).willReturn(CART_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(integerEncryptor.encrypt(AMOUNT, USER_ID_FROM_ACCESS_TOKEN, CART_ITEM_ID_STRING, COLUMN_AMOUNT)).willReturn(ENCRYPTED_AMOUNT);

        CartItemEntity result = underTest.convertDomain(domain);

        assertThat(result)
            .returns(CART_ITEM_ID_STRING, CartItemEntity::getCartItemId)
            .returns(USER_ID_STRING, CartItemEntity::getUserId)
            .returns(CART_ID_STRING, CartItemEntity::getCartId)
            .returns(STOCK_ITEM_ID_STRING, CartItemEntity::getStockItemId)
            .returns(ENCRYPTED_AMOUNT, CartItemEntity::getAmount);
    }

    @Test
    void convertEntity() {
        CartItemEntity entity = CartItemEntity.builder()
            .cartItemId(CART_ITEM_ID_STRING)
            .userId(USER_ID_STRING)
            .cartId(CART_ID_STRING)
            .stockItemId(STOCK_ITEM_ID_STRING)
            .amount(ENCRYPTED_AMOUNT)
            .build();

        given(uuidConverter.convertEntity(CART_ITEM_ID_STRING)).willReturn(CART_ITEM_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(CART_ID_STRING)).willReturn(CART_ID);
        given(uuidConverter.convertEntity(STOCK_ITEM_ID_STRING)).willReturn(STOCK_ITEM_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(integerEncryptor.decrypt(ENCRYPTED_AMOUNT, USER_ID_FROM_ACCESS_TOKEN, CART_ITEM_ID_STRING, COLUMN_AMOUNT)).willReturn(AMOUNT);

        CartItem result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(CART_ITEM_ID, CartItem::getCartItemId)
            .returns(USER_ID, CartItem::getUserId)
            .returns(CART_ID, CartItem::getCartId)
            .returns(STOCK_ITEM_ID, CartItem::getStockItemId)
            .returns(AMOUNT, CartItem::getAmount);
    }
}