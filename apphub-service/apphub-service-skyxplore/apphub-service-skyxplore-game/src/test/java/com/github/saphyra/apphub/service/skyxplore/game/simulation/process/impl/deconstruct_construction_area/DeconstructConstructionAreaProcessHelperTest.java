package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.construction_area.building_module.DeconstructBuildingModuleService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeconstructConstructionAreaProcessHelperTest {
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private WorkProcessFactory workProcessFactory;

    @Mock
    private DeconstructBuildingModuleService deconstructBuildingModuleService;

    @InjectMocks
    private DeconstructConstructionAreaProcessHelper underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Processes processes;

    @Mock
    private WorkProcess process;

    @Mock
    private ProcessModel processModel;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Test
    void initiateDeconstructModules_alreadyDeconstructed() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(deconstruction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.of(deconstruction));

        underTest.initiateDeconstructModules(game, DECONSTRUCTION_ID);

        then(deconstructBuildingModuleService).shouldHaveNoInteractions();
    }

    @Test
    void initiateDeconstructModules() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(deconstruction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);
        given(buildingModules.getByConstructionAreaId(CONSTRUCTION_AREA_ID)).willReturn(List.of(buildingModule));
        given(buildingModule.getBuildingModuleId()).willReturn(BUILDING_MODULE_ID);
        given(deconstructions.findByExternalReference(BUILDING_MODULE_ID)).willReturn(Optional.empty());
        given(buildingModule.getLocation()).willReturn(LOCATION);

        underTest.initiateDeconstructModules(game, DECONSTRUCTION_ID);

        then(deconstructBuildingModuleService).should().deconstructBuildingModule(game, LOCATION, BUILDING_MODULE_ID);
    }

    @Test
    void finishDeconstruction() {
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(deconstruction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);
        given(constructionAreas.findByIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);

        underTest.finishDeconstruction(progressDiff, gameData, DECONSTRUCTION_ID);

        then(constructionAreas).should().remove(constructionArea);
        then(deconstructions).should().remove(deconstruction);
        then(progressDiff).should().delete(CONSTRUCTION_AREA_ID, GameItemType.CONSTRUCTION_AREA);
        then(progressDiff).should().delete(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
    }
}