package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class CreateCartService {
    private final ContactDao contactDao;
    private final CartDao cartDao;
    private final CartFactory cartFactory;

    public void create(UUID userId, UUID contactId) {
        contactDao.findByIdValidated(contactId);

        Cart cart = cartFactory.create(userId, contactId);

        cartDao.save(cart);
    }
}
