package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.DoubleEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class CartConverter extends ConverterBase<CartEntity, Cart> {
    static final String COLUMN_CREATED_AT = "created_at";
    static final String COLUMN_FINALIZED = "finalized";
    static final String COLUMN_MARGIN = "margin";

    private final UuidConverter uuidConverter;
    private final BooleanEncryptor booleanEncryptor;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;
    private final DoubleEncryptor doubleEncryptor;

    @Override
    protected CartEntity processDomainConversion(Cart domain) {
        String userId = accessTokenProvider.getUserIdAsString();
        String cartId = uuidConverter.convertDomain(domain.getCartId());

        return CartEntity.builder()
            .cartId(cartId)
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .contactId(uuidConverter.convertDomain(domain.getContactId()))
            .createdAt(stringEncryptor.encrypt(domain.getCreatedAt().toString(), userId, cartId, COLUMN_CREATED_AT))
            .finalized(booleanEncryptor.encrypt(domain.isFinalized(), userId, cartId, COLUMN_FINALIZED))
            .margin(doubleEncryptor.encrypt(domain.getMargin(), userId, cartId, COLUMN_MARGIN))
            .build();
    }

    @Override
    protected Cart processEntityConversion(CartEntity entity) {
        String userId = accessTokenProvider.getUserIdAsString();

        return Cart.builder()
            .cartId(uuidConverter.convertEntity(entity.getCartId()))
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .contactId(uuidConverter.convertEntity(entity.getContactId()))
            .createdAt(LocalDate.parse(stringEncryptor.decrypt(entity.getCreatedAt(), userId, entity.getCartId(), COLUMN_CREATED_AT)))
            .finalized(booleanEncryptor.decrypt(entity.getFinalized(), userId, entity.getCartId(), COLUMN_FINALIZED))
            .margin(Optional.ofNullable(entity.getMargin()).map(s -> doubleEncryptor.decrypt(s, userId, entity.getCartId(), COLUMN_MARGIN)).orElse(1d))
            .build();
    }
}
