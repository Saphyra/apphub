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
class underTestTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user_id_string";
    private static final UUID CART_ID = UUID.randomUUID();
    private static final String CART_ID_STRING = "cart_id_string";

    @Mock
    private CartRepository repository;

    @Mock
    private CartConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private Cart cart;

    @Mock
    private CartEntity cartEntity;

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

        cartEntities.add(cartEntity);
        cartEntities.add(cartEntity);

        carts.add(cart);
        carts.add(cart);

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(cartEntities);
        given(converter.convertEntity(cartEntities)).willReturn(carts);

        // When
        List<Cart> result = underTest.getByUserId(USER_ID);

        // Then
        assertThat(result).containsExactly(cart, cart);
    }

    @Test
    void findByIdValidated() {
        // Given
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(repository.findById(CART_ID_STRING)).willReturn(Optional.of(cartEntity));
        given(converter.convertEntity(Optional.of(cartEntity))).willReturn(Optional.of(cart));

        // When
        Cart result = underTest.findByIdValidated(CART_ID);

        // Then
        assertThat(result).isEqualTo(cart);
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
}