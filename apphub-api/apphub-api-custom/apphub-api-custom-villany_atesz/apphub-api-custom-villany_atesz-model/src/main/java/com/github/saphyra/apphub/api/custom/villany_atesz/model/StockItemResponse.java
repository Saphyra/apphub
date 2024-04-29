package com.github.saphyra.apphub.api.custom.villany_atesz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StockItemResponse {
    private UUID stockItemId;
    private StockCategoryModel category;
    private String name;
    private String serialNumber;
    private Integer inCar;
    private Integer inStorage;
    //TODO add price
    //TODO add in cart
}
