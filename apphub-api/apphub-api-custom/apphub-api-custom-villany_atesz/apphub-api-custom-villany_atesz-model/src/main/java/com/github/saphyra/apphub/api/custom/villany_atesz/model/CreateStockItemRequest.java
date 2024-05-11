package com.github.saphyra.apphub.api.custom.villany_atesz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateStockItemRequest {
    private UUID stockCategoryId;
    private String name;
    private String serialNumber;
    private Integer inCar;
    private Integer inStorage;
    private Integer price;
}
