package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private String location;
    private String locationType;
    private String assignee;
    private String externalReference;
    private String dataId;
    private Integer amount;
    private Integer requiredWorkPoints;
    private Integer currentWorkPoints;
}
