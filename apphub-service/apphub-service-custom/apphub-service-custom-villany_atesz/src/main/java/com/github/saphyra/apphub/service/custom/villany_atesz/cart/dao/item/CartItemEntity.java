package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.item;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(schema = "villany_atesz", name = "cart_item")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CartItemEntity {
    @Id
    private String cartItemId;
    private String userId;
    private String cartId;
    private String stockItemId;
    private String amount;
}
