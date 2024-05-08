package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CartItemDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID CART_ID = UUID.randomUUID();
    private static final String CART_ID_STRING = "cart-id";
    private static final UUID STOCK_ITEM_ID = UUID.randomUUID();
    private static final String STOCK_ITEM_ID_STRING = "stock-item-id";
    private static final UUID CART_ITEM_ID = UUID.randomUUID();
    private static final String CART_ITEM_ID_STRING = "cart-item-id";

    @Mock
    private CartItemConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CartItemRepository repository;

    @InjectMocks
    private CartItemDao underTest;

    @Mock
    private CartItem domain;

    @Mock
    private CartItemEntity entity;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void getByCartId() {
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(repository.getByCartId(CART_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByCartId(CART_ID)).containsExactly(domain);
    }

    @Test
    void getByCartIdAndStockItemId() {
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);
        given(repository.getByCartIdAndStockItemId(CART_ID_STRING, STOCK_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByCartIdAndStockItemId(CART_ID, STOCK_ITEM_ID)).containsExactly(domain);
    }

    @Test
    void deleteByUserIdAndCartId() {
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserIdAndCartId(USER_ID, CART_ID);

        then(repository).should().deleteByUserIdAndCartId(USER_ID_STRING, CART_ID_STRING);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(CART_ITEM_ID)).willReturn(CART_ITEM_ID_STRING);
        given(repository.findById(CART_ITEM_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(CART_ITEM_ID)).isEqualTo(domain);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(CART_ITEM_ID)).willReturn(CART_ITEM_ID_STRING);
        given(repository.findById(CART_ITEM_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(CART_ITEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void deleteByCartIdAndStockItemId() {
        given(uuidConverter.convertDomain(CART_ID)).willReturn(CART_ID_STRING);
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);

        underTest.deleteByCartIdAndStockItemId(CART_ID, STOCK_ITEM_ID);

        then(repository).should().deleteByCartIdAndStockItemId(CART_ID_STRING, STOCK_ITEM_ID_STRING);
    }

    @Test
    void getByStockItemId() {
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);
        given(repository.getByStockItemId(STOCK_ITEM_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByStockItemId(STOCK_ITEM_ID)).containsExactly(domain);
    }

    @Test
    void deleteByStockItemId() {
        given(uuidConverter.convertDomain(STOCK_ITEM_ID)).willReturn(STOCK_ITEM_ID_STRING);

        underTest.deleteByStockItemId(STOCK_ITEM_ID);

        then(repository).should().deleteByStockItemId(STOCK_ITEM_ID_STRING);
    }
}