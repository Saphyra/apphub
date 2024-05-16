package com.github.saphyra.apphub.integration.structure.api.villany_atesz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartView {
    private UUID cartId;
    private ContactModel contact;
    private Integer totalPrice;
    private Double margin;
    private List<CartItemView> items;
}
