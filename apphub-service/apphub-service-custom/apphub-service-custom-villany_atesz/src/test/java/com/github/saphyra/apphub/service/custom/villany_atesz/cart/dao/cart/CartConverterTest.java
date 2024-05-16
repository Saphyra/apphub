package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.DoubleEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.custom.villany_atesz.VillanyAteszConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartConverter.COLUMN_CREATED_AT;
import static com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartConverter.COLUMN_FINALIZED;
import static com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartConverter.COLUMN_MARGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CartConverterTest {
    private static final UUID CART_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final LocalDate CREATED_AT = LocalDate.now();
    private static final String CART_ID_STRING = "cart-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_CREATED_AT = "encrypted-created-at";
    private static final String ENCRYPTED_FINALIZED = "encrypted-finalized";
    private static final String CONTACT_ID_STRING = "contact-id";
    private static final double MARGIN = 3.5;
    private static final String ENCRYPTED_MARGIN = "encrypted-margin";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private DoubleEncryptor doubleEncryptor;

    @InjectMocks
    private CartConverter underTest;

    @Test
    void convertDomain() {
        Cart domain = Cart.builder()
            .cartId(CART_ID)
            .userId(USER_ID)
            .contactId(CONTACT_ID)
            .createdAt(CREATED_AT)
            .finalized(true)
            .margin(MARGIN)
            .build();

        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(CONTACT_ID)).willReturn(CONTACT_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.encrypt(CREATED_AT.toString(), USER_ID_FROM_ACCESS_TOKEN, CART_ID_STRING, COLUMN_CREATED_AT)).willReturn(ENCRYPTED_CREATED_AT);
        given(booleanEncryptor.encrypt(true, USER_ID_FROM_ACCESS_TOKEN, CART_ID_STRING, COLUMN_FINALIZED)).willReturn(ENCRYPTED_FINALIZED);
        given(doubleEncryptor.encrypt(MARGIN, USER_ID_FROM_ACCESS_TOKEN, CART_ID_STRING, COLUMN_MARGIN)).willReturn(ENCRYPTED_MARGIN);

        CartEntity result = underTest.convertDomain(domain);

        assertThat(result)
            .returns(CART_ID_STRING, CartEntity::getCartId)
            .returns(USER_ID_STRING, CartEntity::getUserId)
            .returns(CONTACT_ID_STRING, CartEntity::getContactId)
            .returns(ENCRYPTED_CREATED_AT, CartEntity::getCreatedAt)
            .returns(ENCRYPTED_MARGIN, CartEntity::getMargin)
            .returns(ENCRYPTED_FINALIZED, CartEntity::getFinalized);
    }

    @Test
    void convertEntity() {
        CartEntity entity = CartEntity.builder()
            .cartId(CART_ID_STRING)
            .userId(USER_ID_STRING)
            .contactId(CONTACT_ID_STRING)
            .createdAt(ENCRYPTED_CREATED_AT)
            .finalized(ENCRYPTED_FINALIZED)
            .margin(ENCRYPTED_MARGIN)
            .build();

        given(uuidConverter.convertEntity(CART_ID_STRING)).willReturn(CART_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(CONTACT_ID_STRING)).willReturn(CONTACT_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.decrypt(ENCRYPTED_CREATED_AT, USER_ID_FROM_ACCESS_TOKEN, CART_ID_STRING, COLUMN_CREATED_AT)).willReturn(CREATED_AT.toString());
        given(booleanEncryptor.decrypt(ENCRYPTED_FINALIZED, USER_ID_FROM_ACCESS_TOKEN, CART_ID_STRING, COLUMN_FINALIZED)).willReturn(true);
        given(doubleEncryptor.decrypt(ENCRYPTED_MARGIN, USER_ID_FROM_ACCESS_TOKEN, CART_ID_STRING, COLUMN_MARGIN)).willReturn(MARGIN);

        Cart result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(CART_ID, Cart::getCartId)
            .returns(USER_ID, Cart::getUserId)
            .returns(CONTACT_ID, Cart::getContactId)
            .returns(CREATED_AT, Cart::getCreatedAt)
            .returns(MARGIN, Cart::getMargin)
            .returns(true, Cart::isFinalized);
    }

    @Test
    void convertEntity_nullMargin() {
        CartEntity entity = CartEntity.builder()
            .cartId(CART_ID_STRING)
            .userId(USER_ID_STRING)
            .contactId(CONTACT_ID_STRING)
            .createdAt(ENCRYPTED_CREATED_AT)
            .finalized(ENCRYPTED_FINALIZED)
            .margin(null)
            .build();

        given(uuidConverter.convertEntity(CART_ID_STRING)).willReturn(CART_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(CONTACT_ID_STRING)).willReturn(CONTACT_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.decrypt(ENCRYPTED_CREATED_AT, USER_ID_FROM_ACCESS_TOKEN, CART_ID_STRING, COLUMN_CREATED_AT)).willReturn(CREATED_AT.toString());
        given(booleanEncryptor.decrypt(ENCRYPTED_FINALIZED, USER_ID_FROM_ACCESS_TOKEN, CART_ID_STRING, COLUMN_FINALIZED)).willReturn(true);

        Cart result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(CART_ID, Cart::getCartId)
            .returns(USER_ID, Cart::getUserId)
            .returns(CONTACT_ID, Cart::getContactId)
            .returns(CREATED_AT, Cart::getCreatedAt)
            .returns(VillanyAteszConstants.DEFAULT_CART_MARGIN, Cart::getMargin)
            .returns(true, Cart::isFinalized);
    }
}