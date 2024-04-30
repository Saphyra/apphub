package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
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

    public List<CartItem> getByStockItemId(UUID stockItemId) {
        return converter.convertEntity(repository.getByStockItemId(uuidConverter.convertDomain(stockItemId)));
    }
}
