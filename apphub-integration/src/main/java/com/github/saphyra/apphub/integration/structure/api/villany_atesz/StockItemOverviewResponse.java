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
public class StockItemOverviewResponse {
    private UUID stockItemId;
    private StockCategoryModel category;
    private String name;
    private String serialNumber;
    private Integer inCar;
    private Integer inCart;
    private Integer inStorage;
    private Integer price;
}
