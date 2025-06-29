package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeconstructBuildingModuleProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 100;

    @Mock
    private WorkProcessFactory workProcessFactory;

    @Mock
    private GameProperties gameProperties;

    @InjectMocks
    private DeconstructBuildingModuleProcessHelper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Mock
    private Game game;

    @Mock
    private DeconstructionProperties deconstructionProperties;

    @Test
    void startWork() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getLocation()).willReturn(LOCATION);
        given(gameProperties.getDeconstruction()).willReturn(deconstructionProperties);
        given(deconstructionProperties.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);

        underTest.startWork(game, PROCESS_ID, DECONSTRUCTION_ID);

        then(workProcessFactory).should().save(game, LOCATION, PROCESS_ID, REQUIRED_WORK_POINTS, SkillType.BUILDING);
    }

    @Test
    void finishDeconstruction() {
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_MODULE_ID);
        given(buildingModules.findByIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);

        underTest.finishDeconstruction(progressDiff, gameData, DECONSTRUCTION_ID);

        then(deconstructions).should().remove(deconstruction);
        then(buildingModules).should().remove(buildingModule);
        then(progressDiff).should().delete(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
        then(progressDiff).should().delete(BUILDING_MODULE_ID, GameItemType.BUILDING_MODULE);
    }
}