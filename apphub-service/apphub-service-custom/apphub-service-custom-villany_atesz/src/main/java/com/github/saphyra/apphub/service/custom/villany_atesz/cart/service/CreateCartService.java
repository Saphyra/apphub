package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.dao.ContactDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateCartService {
    private final ContactDao contactDao;
    private final CartDao cartDao;
    private final CartFactory cartFactory;

    public UUID create(UUID userId, UUID contactId) {
        ValidationUtil.notNull(contactId, "contactId");

        contactDao.findByIdValidated(contactId);

        Cart cart = cartFactory.create(userId, contactId);

        cartDao.save(cart);

        return cart.getCartId();
    }
}
