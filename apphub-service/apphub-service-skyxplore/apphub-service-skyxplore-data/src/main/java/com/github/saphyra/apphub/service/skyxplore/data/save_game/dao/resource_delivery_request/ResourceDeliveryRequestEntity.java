package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.resource_delivery_request;

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
@Table(schema = "skyxplore_game", name = "resource_delivery_request")
class ResourceDeliveryRequestEntity {
    @Id
    private String resourceDeliveryRequestId;
    private String gameId;
    private String reservedStorageId;
}
