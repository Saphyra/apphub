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
public class StockItemInventoryResponse {
    private UUID stockItemId;
    private UUID stockCategoryId;
    private String name;
    private String serialNumber;
    private String barCode;
    private Integer inCar;
    private Integer inStorage;
    private Boolean inCart;
    private Boolean inventoried;
}
