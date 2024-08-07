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
public class AddToStockRequest {
    private UUID stockItemId;
    private Integer inCar;
    private Integer inStorage;
    private Integer price;
    private String barCode;
    private Boolean forceUpdatePrice;
}
