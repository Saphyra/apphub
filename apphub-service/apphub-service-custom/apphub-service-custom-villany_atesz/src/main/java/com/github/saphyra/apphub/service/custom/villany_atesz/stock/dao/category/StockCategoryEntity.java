package com.github.saphyra.apphub.service.custom.villany_atesz.stock.dao.category;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(schema = "villany_atesz", name = "stock_category")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
class StockCategoryEntity {
    @Id
    private String stockCategoryId;
    private String userId;
    private String name;
    private String measurement;
}
