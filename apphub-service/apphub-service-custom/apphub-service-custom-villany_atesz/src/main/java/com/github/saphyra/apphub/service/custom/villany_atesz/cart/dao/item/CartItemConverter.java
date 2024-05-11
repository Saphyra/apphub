package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CartItemConverter extends ConverterBase<CartItemEntity, CartItem> {
    static final String COLUMN_AMOUNT = "amount";

    private final UuidConverter uuidConverter;
    private final AccessTokenProvider accessTokenProvider;
    private final IntegerEncryptor integerEncryptor;

    @Override
    protected CartItemEntity processDomainConversion(CartItem domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String cartItemId = uuidConverter.convertDomain(domain.getCartItemId());

        return CartItemEntity.builder()
            .cartItemId(cartItemId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .cartId(uuidConverter.convertDomain(domain.getCartId()))
            .stockItemId(uuidConverter.convertDomain(domain.getStockItemId()))
            .amount(integerEncryptor.encrypt(domain.getAmount(), userId, cartItemId, COLUMN_AMOUNT))
            .build();
    }

    @Override
    protected CartItem processEntityConversion(CartItemEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return CartItem.builder()
            .cartItemId(uuidConverter.convertEntity(entity.getCartItemId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .cartId(uuidConverter.convertEntity(entity.getCartId()))
            .stockItemId(uuidConverter.convertEntity(entity.getStockItemId()))
            .amount(integerEncryptor.decrypt(entity.getAmount(), userId, entity.getCartItemId(), COLUMN_AMOUNT))
            .build();
    }
}
