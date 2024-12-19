package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.annotations.VisibleForTesting;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

@NoArgsConstructor
public class BuildingModules extends Vector<BuildingModule> {
    @VisibleForTesting
    public BuildingModules(BuildingModule... modules) {
        super(Arrays.asList(modules));
    }

    public List<BuildingModule> getByLocation(UUID location) {
        return stream()
            .filter(buildingModule -> buildingModule.getLocation().equals(location))
            .toList();
    }

    public List<BuildingModule> getByLocationAndDataId(UUID location, String dataId) {
        return getByLocation(location)
            .stream()
            .filter(buildingModule -> buildingModule.getDataId().equals(dataId))
            .toList();
    }

    public List<BuildingModule> getByConstructionAreaId(UUID constructionAreaId) {
        return stream()
            .filter(buildingModule -> buildingModule.getConstructionAreaId().equals(constructionAreaId))
            .collect(Collectors.toList());
    }

    public BuildingModule findByBuildingModuleIdValidated(UUID buildingModuleId) {
        return findByBuildingModuleId(buildingModuleId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "BuildingModule not found by buildingModuleId " + buildingModuleId));
    }

    public Optional<BuildingModule> findByBuildingModuleId(UUID buildingModuleId) {
        return stream()
            .filter(buildingModule -> buildingModule.getBuildingModuleId().equals(buildingModuleId))
            .findAny();
    }
}
