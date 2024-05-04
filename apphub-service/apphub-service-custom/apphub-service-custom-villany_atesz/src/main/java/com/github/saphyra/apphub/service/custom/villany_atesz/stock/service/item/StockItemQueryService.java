package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemForCategoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPrice;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.price.StockItemPriceDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.category.StockCategoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StockItemQueryService {
    private final StockItemDao stockItemDao;
    private final StockCategoryQueryService stockCategoryQueryService;
    private final StockItemPriceDao stockItemPriceDao;
    private final CartItemDao cartItemDao;
    private final CartDao cartDao;

    public List<StockItemOverviewResponse> getStockItems(UUID userId) {
        return stockItemDao.getByUserId(userId)
            .stream()
            .map(this::convert)
            .toList();
    }

    private StockItemOverviewResponse convert(StockItem stockItem) {
        return StockItemOverviewResponse.builder()
            .stockItemId(stockItem.getStockItemId())
            .category(stockCategoryQueryService.findByStockCategoryId(stockItem.getStockCategoryId()))
            .name(stockItem.getName())
            .serialNumber(stockItem.getSerialNumber())
            .inCar(stockItem.getInCar())
            .inCart(countInCart(stockItem.getUserId(), stockItem.getStockItemId()))
            .inStorage(stockItem.getInStorage())
            .price(getPrice(stockItem.getStockItemId()))
            .build();
    }

    private Integer countInCart(UUID userId, UUID stockItemId) {
        return cartDao.getByUserId(userId)
            .stream()
            .filter(cart -> !cart.isFinalized())
            .flatMap(cart -> cartItemDao.getByCartIdAndStockItemId(cart.getCartId(), stockItemId).stream())
            .mapToInt(CartItem::getAmount)
            .sum();
    }

    private Integer getPrice(UUID stockItemId) {
        return stockItemPriceDao.getByStockItemId(stockItemId)
            .stream()
            .map(StockItemPrice::getPrice)
            .max(Integer::compareTo)
            .orElse(0);
    }

    public List<StockItemForCategoryResponse> getForCategory(UUID stockCategoryId) {
        return stockItemDao.getByStockCategoryId(stockCategoryId)
            .stream()
            .map(stockItem -> StockItemForCategoryResponse.builder()
                .stockItemId(stockItem.getStockItemId())
                .name(stockItem.getName())
                .build())
            .collect(Collectors.toList());
    }
}
