package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CartRepository extends CrudRepository<CartEntity, String> {
    void deleteByUserId(String userId);

    List<CartEntity> getByUserId(String userId);

    void deleteByUserIdAndCartId(String userId, String cartId);

    List<CartEntity> getByUserIdAndContactId(String userId, String contactId);
}
