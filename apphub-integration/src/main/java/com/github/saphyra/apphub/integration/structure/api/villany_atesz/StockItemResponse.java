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
public class StockItemResponse {
    private UUID stockItemId;
    private UUID stockCategoryId;
    private String name;
    private String serialNumber;
    private String barCode;
    private Integer inCar;
    private Integer inStorage;
    private Boolean inventoried;
    private Boolean markedForAcquisition;
}
