package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface CartRepository extends CrudRepository<CartEntity, String> {
    void deleteByUserId(String userId);

    List<CartEntity> getByUserId(String userId);
}
