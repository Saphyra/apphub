package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class StockItem {
    private final UUID stockItemId;
    private final UUID userId;
    private UUID stockCategoryId;
    private String name;
    private String serialNumber;
    private String barCode;
    private int inCar;
    private int inStorage;
    private boolean inventoried;
    private boolean markedForAcquisition;
}
