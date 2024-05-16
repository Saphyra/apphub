package com.github.saphyra.apphub.service.custom.villany_atesz.cart.dao.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Cart {
    private final UUID cartId;
    private final UUID userId;
    private final UUID contactId;
    private final LocalDate createdAt;
    private boolean finalized;
    private double margin;
}
