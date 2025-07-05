package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CartItemDao extends AbstractDao<CartItemEntity, CartItem, String, CartItemRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    CartItemDao(CartItemConverter converter, CartItemRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public List<CartItem> getByCartId(UUID cartId) {
        return converter.convertEntity(repository.getByCartId(uuidConverter.convertDomain(cartId)));
    }

    public List<CartItem> getByCartIdAndStockItemId(UUID cartId, UUID stockItemId) {
        return converter.convertEntity(repository.getByCartIdAndStockItemId(uuidConverter.convertDomain(cartId), uuidConverter.convertDomain(stockItemId)));
    }

    public void deleteByUserIdAndCartId(UUID userId, UUID cartId) {
        repository.deleteByUserIdAndCartId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(cartId));
    }

    public CartItem findByIdValidated(UUID cartItemId) {
        return findById(uuidConverter.convertDomain(cartItemId))
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "CartItem not found by id " + cartItemId));
    }

    public void deleteByCartIdAndStockItemId(UUID cartId, UUID stockItemId) {
        repository.deleteByCartIdAndStockItemId(uuidConverter.convertDomain(cartId), uuidConverter.convertDomain(stockItemId));
    }

    public List<CartItem> getByStockItemId(UUID stockItemId) {
        return converter.convertEntity(repository.getByStockItemId(uuidConverter.convertDomain(stockItemId)));
    }

    public void deleteByStockItemId(UUID stockItemId) {
        repository.deleteByStockItemId(uuidConverter.convertDomain(stockItemId));
    }
}
