package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.item;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(schema = "villany_atesz", name = "stock_item")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
class StockItemEntity {
    @Id
    private String stockItemId;
    private String userId;
    private String stockCategoryId;
    private String name;
    private String serialNumber;
    private String inCar;
    private String inStorage;
}
