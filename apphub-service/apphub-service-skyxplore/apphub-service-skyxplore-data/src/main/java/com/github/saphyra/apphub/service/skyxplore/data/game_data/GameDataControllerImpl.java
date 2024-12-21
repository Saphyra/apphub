package com.github.saphyra.apphub.service.skyxplore.data.game_data;

import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreGameDataController;
import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStat;
import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.CitizenStatsAndSkills;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.data.AbstractDataService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.GameDataItem;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.resource.ResourceDataService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilities;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.terraforming.TerraformingPossibilitiesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class GameDataControllerImpl implements SkyXploreGameDataController {
    private final Map<String, ? extends GameDataItem> items;
    private final TerraformingPossibilitiesService terraformingPossibilitiesService;
    private final List<String> resources;
    private final ConstructionAreaDataService constructionAreaDataService;

    public GameDataControllerImpl(
        List<AbstractDataService<?, ? extends GameDataItem>> dataServices,
        TerraformingPossibilitiesService terraformingPossibilitiesService,
        ResourceDataService resourceDataService,
        ConstructionAreaDataService constructionAreaDataService) {
        this.items = dataServices.stream()
            .flatMap(dataService -> dataService.values().stream())
            .collect(Collectors.toMap(GameDataItem::getId, Function.identity()));
        this.terraformingPossibilitiesService = terraformingPossibilitiesService;

        this.resources = resourceDataService.keySet()
            .stream()
            .toList();
        this.constructionAreaDataService = constructionAreaDataService;
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
        return Collections.emptyList();
    }

    @Override
    public List<Object> getTerraformingPossibilities(String surfaceTypeString) {
        SurfaceType surfaceType = ValidationUtil.convertToEnumChecked(surfaceTypeString, SurfaceType::valueOf, "surfaceType");

        return new ArrayList<>(terraformingPossibilitiesService.getOptional(surfaceType)
            .orElse(new TerraformingPossibilities()));
    }

    @Override
    public List<Object> getAvailableConstructionAreas(String surfaceTypeString) {
        SurfaceType surfaceType = ValidationUtil.convertToEnumChecked(surfaceTypeString, SurfaceType::valueOf, "surfaceType");

        return constructionAreaDataService.values()
            .stream()
            .filter(constructionAreaData -> constructionAreaData.getSupportedSurfaces().contains(surfaceType))
            .collect(Collectors.toList());
    }

    @Override
    public CitizenStatsAndSkills getStatsAndSkills() {
        return CitizenStatsAndSkills.builder()
            .stats(Arrays.stream(CitizenStat.values()).map(Enum::name).toList())
            .skills(Arrays.stream(SkillType.values()).map(Enum::name).toList())
            .build();
    }

    @Override
    public List<String> getResourceDataIds() {
        return resources;
    }
}
