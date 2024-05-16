package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(schema = "villany_atesz", name = "cart")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CartEntity {
    @Id
    private String cartId;
    private String userId;
    private String contactId;
    private String createdAt;
    private String finalized;
    private String margin;
}
