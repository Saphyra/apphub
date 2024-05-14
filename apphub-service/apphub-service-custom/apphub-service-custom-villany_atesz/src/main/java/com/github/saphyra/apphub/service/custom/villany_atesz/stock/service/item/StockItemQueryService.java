package com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.item;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemAcquisitionResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemForCategoryResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.StockItemOverviewResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockItemQueryService {
    private final StockItemDao stockItemDao;
    private final StockCategoryQueryService stockCategoryQueryService;
    private final StockItemPriceDao stockItemPriceDao;
    private final CartItemDao cartItemDao;
    private final CartDao cartDao;

    public List<StockItemForCategoryResponse> getForCategory(UUID stockCategoryId) {
        return stockItemDao.getByStockCategoryId(stockCategoryId)
            .stream()
            .map(stockItem -> StockItemForCategoryResponse.builder()
                .stockItemId(stockItem.getStockItemId())
                .name(stockItem.getName())
                .build())
            .collect(Collectors.toList());
    }

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
            .barCode(stockItem.getBarCode())
            .inCar(stockItem.getInCar())
            .inCart(countInCart(stockItem.getStockItemId()))
            .inStorage(stockItem.getInStorage())
            .price(getPrice(stockItem.getStockItemId()))
            .build();
    }

    private Integer countInCart(UUID stockItemId) {
        return cartItemDao.getByStockItemId(stockItemId)
            .stream()
            .filter(cartItem -> !cartDao.findByIdValidated(cartItem.getCartId()).isFinalized())
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

    public Optional<StockItemAcquisitionResponse> findByBarCode(UUID userId, String barCode) {
        ValidationUtil.notNull(barCode, "barCode");

        return stockItemDao.getByUserId(userId)
            .stream()
            .filter(stockItem -> barCode.equals(stockItem.getBarCode()))
            .findFirst()
            .map(stockItem -> StockItemAcquisitionResponse.builder()
                .stockCategoryId(stockItem.getStockCategoryId())
                .stockItemId(stockItem.getStockItemId())
                .barCode(stockItem.getBarCode())
                .build());
    }

    public String findBarCodeByStockItemId(UUID stockItemId) {
        return stockItemDao.findByIdValidated(stockItemId)
            .getBarCode();
    }
}
