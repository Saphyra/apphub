package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CartItemRepository extends CrudRepository<CartItemEntity, String> {
    void deleteByUserId(String userId);

    List<CartItemEntity> getByCartId(String cartId);

    List<CartItemEntity> getByCartIdAndStockItemId(String cartId, String stockItemId);

    void deleteByUserIdAndCartId(String userId, String cartId);

    void deleteByCartIdAndStockItemId(String cartId, String stockItemId);
}
