package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_module;

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
@Table(schema = "skyxplore_game", name = "building_module")
class BuildingModuleEntity {
    @Id
    private String buildingModuleId;
    private String gameId;
    private String location;
    private String constructionAreaId;
    private String dataId;
}
