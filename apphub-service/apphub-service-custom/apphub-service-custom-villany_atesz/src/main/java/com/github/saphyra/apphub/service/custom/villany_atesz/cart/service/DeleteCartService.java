package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteCartService {
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;

    @Transactional
    public void delete(UUID userId, UUID cartId) {
        cartDao.deleteByUserIdAndCartId(userId, cartId);
        cartItemDao.deleteByUserIdAndCartId(userId, cartId);
    }

    public void deleteForContact(UUID userId, UUID contactId) {
        cartDao.getByUserIdAndContactId(userId, contactId)
            .forEach(cart -> delete(userId, cart.getCartId()));
    }
}
