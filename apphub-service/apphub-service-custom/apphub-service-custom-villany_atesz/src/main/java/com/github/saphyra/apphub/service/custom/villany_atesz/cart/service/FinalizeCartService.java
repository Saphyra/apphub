package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class FinalizeCartService {
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private final StockItemDao stockItemDao;

    @Transactional
    public void finalizeCart(UUID cartId) {
        Cart cart = cartDao.findByIdValidated(cartId);

        if (cart.isFinalized()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, "Cart " + cartId + " is already finalized.");
        }

        cart.setFinalized(true);
        cartDao.save(cart);

        cartItemDao.getByCartId(cartId)
            .forEach(this::process);
    }

    private void process(CartItem cartItem) {
        StockItem stockItem = stockItemDao.findByIdValidated(cartItem.getStockItemId());
        stockItem.setInCar(stockItem.getInCar() - cartItem.getAmount());

        stockItemDao.save(stockItem);
    }
}
