package com.github.saphyra.apphub.service.skyxplore.data.save_game.building;

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
@Table(schema = "skyxplore_game", name = "building")
class BuildingEntity {
    @Id
    private String buildingId;
    private String gameId;
    private String surfaceId;
    private String dataId;
    private int level;
}
