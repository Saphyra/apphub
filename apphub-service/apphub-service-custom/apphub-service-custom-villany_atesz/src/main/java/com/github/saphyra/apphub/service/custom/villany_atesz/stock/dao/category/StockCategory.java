package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class StockCategory {
    private final UUID stockCategoryId;
    private final UUID userId;
    private String name;
    private String measurement;
}
