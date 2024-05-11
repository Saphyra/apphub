package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CartDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user_id_string";
    private static final UUID CART_ID = UUID.randomUUID();
    private static final String CART_ID_STRING = "cart_id_string";
    private static final UUID CONTACT_ID = UUID.randomUUID();
    private static final String CONTACT_ID_STRING = "contact-id";

    @Mock
    private CartRepository repository;

    @Mock
    private CartConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private Cart domain;

    @Mock
    private CartEntity entity;

    @InjectMocks
    private CartDao underTest;

    @Test
    void deleteByUserId() {
        // Given
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        // When
        underTest.deleteByUserId(USER_ID);

        // Then
        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void getByUserId() {
        // Given
        List<CartEntity> cartEntities = new ArrayList<>();
        List<Cart> carts = new ArrayList<>();

        cartEntities.add(entity);
        cartEntities.add(entity);

        carts.add(domain);
        carts.add(domain);

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(cartEntities);
        given(converter.convertEntity(cartEntities)).willReturn(carts);

        // When
        List<Cart> result = underTest.getByUserId(USER_ID);

        // Then
        assertThat(result).containsExactly(domain, domain);
    }

    @Test
    void findByIdValidated() {
        // Given
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(repository.findById(CART_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        // When
        Cart result = underTest.findByIdValidated(CART_ID);

        // Then
        assertThat(result).isEqualTo(domain);
    }

    @Test
    void findByIdValidated_notFound() {
        // Given
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(repository.findById(CART_ID_STRING)).willReturn(Optional.empty());

        // When
        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(CART_ID));

        // Then
        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void deleteByUserIdAndCartId() {
        // Given
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);

        // When
        underTest.deleteByUserIdAndCartId(USER_ID, CART_ID);

        // Then
        then(repository).should().deleteByUserIdAndCartId(USER_ID_STRING, CART_ID_STRING);
    }

    @Test
    void getByUserIdAndContactId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(CONTACT_ID)).willReturn(CONTACT_ID_STRING);
        given(repository.getByUserIdAndContactId(USER_ID_STRING, CONTACT_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserIdAndContactId(USER_ID, CONTACT_ID)).containsExactly(domain);
    }
}