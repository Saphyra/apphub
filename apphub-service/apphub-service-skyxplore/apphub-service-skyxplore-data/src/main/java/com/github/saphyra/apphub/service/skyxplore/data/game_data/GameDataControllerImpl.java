package com.github.saphyra.apphub.service.skyxplore.data.game_data;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreGameDataController;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class GameDataControllerImpl implements SkyXploreGameDataController {
    private final Map<String, ? extends GameDataItem> items;
    private final Map<String, ? extends BuildingData> buildings;
    private final TerraformingPossibilitiesService terraformingPossibilitiesService;

    public GameDataControllerImpl(
        List<AbstractDataService<?, ? extends GameDataItem>> dataServices,
        TerraformingPossibilitiesService terraformingPossibilitiesService
    ) {
        this.items = dataServices.stream()
            .flatMap(dataService -> dataService.values().stream())
            .collect(Collectors.toMap(GameDataItem::getId, Function.identity()));
        this.terraformingPossibilitiesService = terraformingPossibilitiesService;

        this.buildings = items.values()
            .stream()
            .filter(gameDataItem -> gameDataItem instanceof BuildingData)
            .map(gameDataItem -> (BuildingData) gameDataItem)
            .collect(Collectors.toMap(GameDataItem::getId, Function.identity()));
    }

    @Override
    public Object getGameData(String dataId) {
        log.info("Querying gameData item with id {}", dataId);
        return Optional.ofNullable(items.get(dataId))
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Data not found with id " + dataId));
    }

    @Override
    public List<String> getAvailableBuildings(String surfaceTypeString) {
        log.info("Querying available buildings for surfaceType {}", surfaceTypeString);

        SurfaceType surfaceType = ValidationUtil.convertToEnumChecked(surfaceTypeString, SurfaceType::valueOf, "surfaceType");

        return buildings.values()
            .stream()
            .filter(buildingData -> buildingData.getPlaceableSurfaceTypes().contains(surfaceType))
            .map(GameDataItem::getId)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getTerraformingPossibilities(String surfaceTypeString) {
        SurfaceType surfaceType = ValidationUtil.convertToEnumChecked(surfaceTypeString, SurfaceType::valueOf, "surfaceType");

        return terraformingPossibilitiesService.getOptional(surfaceType)
            .orElse(new TerraformingPossibilities())
            .stream()
            .map(TerraformingPossibility::getSurfaceType)
            .map(SurfaceType::name)
            .collect(Collectors.toList());
    }
}
