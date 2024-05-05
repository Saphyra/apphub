package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RemoveFromCartService {
    private final CartItemDao cartItemDao;

    @Transactional
    public void removeFromCart(UUID cartId, UUID stockItemId) {
        cartItemDao.deleteByCartIdAndStockItemId(cartId, stockItemId);
    }
}
