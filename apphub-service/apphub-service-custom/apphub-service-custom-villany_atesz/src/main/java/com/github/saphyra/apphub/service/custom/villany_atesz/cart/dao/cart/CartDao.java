package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CartDao extends AbstractDao<CartEntity, Cart, String, CartRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public CartDao(CartConverter converter, CartRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<Cart> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public Cart findByIdValidated(UUID cartId) {
        return findById(cartId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Cart not found by id " + cartId));
    }

    public Optional<Cart> findById(UUID cartId) {
        return findById(uuidConverter.convertDomain(cartId));
    }

    public void deleteByUserIdAndCartId(UUID userId, UUID cartId) {
        repository.deleteByUserIdAndCartId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(cartId));
    }

    public List<Cart> getByUserIdAndContactId(UUID userId, UUID contactId) {
        return converter.convertEntity(repository.getByUserIdAndContactId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(contactId)));
    }
}
