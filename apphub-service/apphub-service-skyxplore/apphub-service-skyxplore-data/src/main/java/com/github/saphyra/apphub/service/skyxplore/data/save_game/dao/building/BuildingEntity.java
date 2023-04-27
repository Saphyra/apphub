package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "building")
class BuildingEntity {
    @Id
    private String buildingId;
    private String gameId;
    private String location;
    private String surfaceId;
    private String dataId;
    private int level;
}
