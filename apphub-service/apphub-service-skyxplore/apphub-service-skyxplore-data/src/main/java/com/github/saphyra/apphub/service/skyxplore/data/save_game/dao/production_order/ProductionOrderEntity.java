package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "production_order")
class ProductionOrderEntity {
    @Id
    private String productionOrderId;
    private String gameId;
    private String productionRequestId;
    private String constructionAreaId;
    private String resourceDataId;
    private Integer requestedAmount;
    private Integer startedAmount;
}
