package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
//TODO unit test
class CartConverter extends ConverterBase<CartEntity, Cart> {
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_FINALIZED = "finalized";

    private final UuidConverter uuidConverter;
    private final BooleanEncryptor booleanEncryptor;
    private final StringEncryptor stringEncryptor;
    private final AccessTokenProvider accessTokenProvider;

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
            .build();
    }
}
