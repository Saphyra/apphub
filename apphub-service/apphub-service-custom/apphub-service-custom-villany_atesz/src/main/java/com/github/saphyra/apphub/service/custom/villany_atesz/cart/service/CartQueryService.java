package com.github.saphyra.apphub.service.custom.villany_atesz.cart.service;

import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartItemView;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartResponse;
import com.github.saphyra.apphub.api.custom.villany_atesz.model.CartView;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.Cart;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart.CartDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItem;
import com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item.CartItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.contacts.service.ContactQueryService;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item.StockItemDao;
import com.github.saphyra.apphub.service.custom.villany_atesz.stock.service.price.StockItemPriceQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CartQueryService {
    private final CartDao cartDao;
    private final ContactQueryService contactQueryService;
    private final CartItemDao cartItemDao;
    private final StockItemPriceQueryService stockItemPriceQueryService;
    private final StockItemDao stockItemDao;

    public List<CartResponse> getCarts(UUID userId) {
        return cartDao.getByUserId(userId)
            .stream()
            .filter(cart -> !cart.isFinalized())
            .map(cart -> CartResponse.builder()
                .cartId(cart.getCartId())
                .contact(contactQueryService.getContact(cart.getContactId()))
                .build())
            .collect(Collectors.toList());
    }

    public CartView getCart(UUID cartId) {
        Cart cart = cartDao.findByIdValidated(cartId);

        List<CartItem> items = cartItemDao.getByCartId(cartId);

        return CartView.builder()
            .cartId(cartId)
            .contact(contactQueryService.getContact(cart.getContactId()))
            .totalPrice(getTotalPrice(items))
            .items(convert(items))
            .margin(cart.getMargin())
            .build();
    }

    public Integer getTotalPrice(List<CartItem> items) {
        return summarizeByStockItemId(items)
            .mapToInt(entry -> stockItemPriceQueryService.getPriceOf(entry.getKey()) * entry.getValue())
            .sum();
    }

    private List<CartItemView> convert(List<CartItem> items) {
        return summarizeByStockItemId(items)
            .map(entry -> CartItemView.builder()
                .stockItemId(entry.getKey())
                .name(stockItemDao.findByIdValidated(entry.getKey()).getName())
                .amount(entry.getValue())
                .build())
            .collect(Collectors.toList());
    }

    private static Stream<Map.Entry<UUID, Integer>> summarizeByStockItemId(List<CartItem> items) {
        return items.stream()
            .collect(Collectors.groupingBy(CartItem::getStockItemId))
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, o -> o.getValue().stream().mapToInt(CartItem::getAmount).sum()))
            .entrySet()
            .stream();
    }
}
