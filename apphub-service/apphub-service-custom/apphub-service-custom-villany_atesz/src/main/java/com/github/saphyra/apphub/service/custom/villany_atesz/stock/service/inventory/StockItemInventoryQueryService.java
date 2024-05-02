package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.inventory;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemInventoryResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
//TODO unit test
public class StockItemInventoryQueryService {
    private final StockItemDao stockItemDao;
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;

    public List<StockItemInventoryResponse> getItems(UUID userId) {
        return stockItemDao.getByUserId(userId)
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private StockItemInventoryResponse convert(StockItem stockItem) {
        return StockItemInventoryResponse.builder()
            .stockItemId(stockItem.getStockItemId())
            .stockCategoryId(stockItem.getStockCategoryId())
            .name(stockItem.getName())
            .serialNumber(stockItem.getSerialNumber())
            .inCar(stockItem.getInCar())
            .inStorage(stockItem.getInStorage())
            .inCart(isInCart(stockItem.getUserId(), stockItem.getStockItemId()))
            .inventoried(stockItem.isInventoried())
            .build();
    }

    private Boolean isInCart(UUID userId, UUID stockItemId) {
        return cartDao.getByUserId(userId)
            .stream()
            .filter(cart -> !cart.isFinalized())
            .flatMap(cart -> cartItemDao.getByCartId(cart.getCartId()).stream().filter(cartItem -> cartItem.getStockItemId().equals(stockItemId)))
            .mapToInt(CartItem::getAmount)
            .sum() > 0;
    }
}
