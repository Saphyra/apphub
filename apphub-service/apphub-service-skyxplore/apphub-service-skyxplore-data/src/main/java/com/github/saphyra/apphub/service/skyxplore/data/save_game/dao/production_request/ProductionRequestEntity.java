package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_request;

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
@Table(schema = "skyxplore_game", name = "production_request")
class ProductionRequestEntity {
    @Id
    private String productionRequestId;
    private String gameId;
    private String reservedStorageId;
    private Integer requestedAmount;
    private Integer dispatchedAmount;
}
