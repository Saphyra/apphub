package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.stored_resource;

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
@Table(schema = "skyxplore_game", name = "stored_resource")
class StoredResourceEntity {
    @Id
    private String storedResourceId;
    private String gameId;
    private String location;
    private String containerId;
    private String containerType;
    private String dataId;
    private int amount;
}
