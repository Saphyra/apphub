package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CreateCartServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONTACT_ID = UUID.randomUUID();

    @Mock
    private ContactDao contactDao;

    @Mock
    private CartDao cartDao;

    @Mock
    private CartFactory cartFactory;

    @InjectMocks
    private CreateCartService underTest;

    @Mock
    private Cart cart;

    @Test
    void nullContactId() {
        ExceptionValidator.validateInvalidParam(() -> underTest.create(USER_ID, null), "contactId", "must not be null");
    }

    @Test
    void create() {
        given(cartFactory.create(USER_ID, CONTACT_ID)).willReturn(cart);

        underTest.create(USER_ID, CONTACT_ID);

        then(contactDao).should().findByIdValidated(CONTACT_ID);
        then(cartDao).should().save(cart);
    }
}