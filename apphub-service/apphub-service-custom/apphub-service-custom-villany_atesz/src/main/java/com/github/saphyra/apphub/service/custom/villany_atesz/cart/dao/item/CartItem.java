package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class CartItem {
    private final UUID cartItemId;
    private final UUID userId;
    private final UUID cartId;
    private final UUID stockItemId;
    private final Integer amount;
}
