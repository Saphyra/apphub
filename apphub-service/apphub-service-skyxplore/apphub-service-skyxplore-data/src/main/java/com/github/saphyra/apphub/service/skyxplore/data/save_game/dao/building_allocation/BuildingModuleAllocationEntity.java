package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

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
@Table(schema = "skyxplore_game", name = "building_module_allocation")
class BuildingModuleAllocationEntity {
    @Id
    private String buildingAllocationId;
    private String gameId;
    private String buildingModuleId;
    private String processId;
}
