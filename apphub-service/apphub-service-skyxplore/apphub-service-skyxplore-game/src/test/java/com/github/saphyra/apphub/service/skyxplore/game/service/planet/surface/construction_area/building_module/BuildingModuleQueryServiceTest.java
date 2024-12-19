package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.ConstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.DeconstructionResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.overview.surface.building.BuildingModuleResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingModuleCategory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.BuildingModuleDataService;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.ConstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.DeconstructionConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BuildingModuleQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @Mock
    private BuildingModuleDataService buildingModuleDataService;

    @Mock
    private GameDao gameDao;

    @Mock
    private ConstructionConverter constructionConverter;

    @Mock
    private DeconstructionConverter deconstructionConverter;

    @InjectMocks
    private BuildingModuleQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private BuildingModuleData buildingModuleData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private ConstructionResponse constructionResponse;

    @Mock
    private DeconstructionResponse deconstructionResponse;

    @Test
    void getBuildingModulesOfConstructionArea() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(buildingModule.getDataId()).willReturn(DATA_ID);
        given(buildingModuleDataService.get(DATA_ID)).willReturn(buildingModuleData);
        given(buildingModuleData.getCategory()).willReturn(BuildingModuleCategory.CULTURAL);
        given(gameData.getConstructions()).willReturn(constructions);
        given(gameData.getConstructions().findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(construction));
        given(constructionConverter.toResponse(construction)).willReturn(constructionResponse);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(deconstruction));
        given(deconstructionConverter.toResponse(deconstruction)).willReturn(deconstructionResponse);

        CustomAssertions.singleListAssertThat(underTest.getBuildingModulesOfConstructionArea(USER_ID, CONSTRUCTION_AREA_ID))
            .returns(BUILDING_MODULE_ID, BuildingModuleResponse::getBuildingModuleId)
            .returns(DATA_ID, BuildingModuleResponse::getDataId)
            .returns(BuildingModuleCategory.CULTURAL.name(), BuildingModuleResponse::getBuildingModuleCategory)
            .returns(constructionResponse, BuildingModuleResponse::getConstruction)
            .returns(deconstructionResponse, BuildingModuleResponse::getDeconstruction);
    }

    @Test
    void getBuildingModulesOfConstructionArea_nulls() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(game.getData()).willReturn(gameData);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(buildingModule.getDataId()).willReturn(DATA_ID);
        given(buildingModuleDataService.get(DATA_ID)).willReturn(buildingModuleData);
        given(buildingModuleData.getCategory()).willReturn(BuildingModuleCategory.CULTURAL);
        given(gameData.getConstructions()).willReturn(constructions);
        given(gameData.getConstructions().findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());

        CustomAssertions.singleListAssertThat(underTest.getBuildingModulesOfConstructionArea(USER_ID, CONSTRUCTION_AREA_ID))
            .returns(BUILDING_MODULE_ID, BuildingModuleResponse::getBuildingModuleId)
            .returns(DATA_ID, BuildingModuleResponse::getDataId)
            .returns(BuildingModuleCategory.CULTURAL.name(), BuildingModuleResponse::getBuildingModuleCategory)
            .returns(null, BuildingModuleResponse::getConstruction)
            .returns(null, BuildingModuleResponse::getDeconstruction);
    }
}