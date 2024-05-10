package com.github.saphyra.apphub.integration.structure.api.villany_atesz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StockCategoryModel {
    private UUID stockCategoryId;
    private String name;
    private String measurement;
}
