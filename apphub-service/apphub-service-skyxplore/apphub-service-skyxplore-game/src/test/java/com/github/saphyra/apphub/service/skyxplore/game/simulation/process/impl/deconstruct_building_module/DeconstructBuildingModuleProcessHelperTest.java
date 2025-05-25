package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DeconstructBuildingModuleProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();

    @Mock
    private WorkProcessFactory workProcessFactory;

    @InjectMocks
    private DeconstructBuildingModuleProcessHelper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Processes processes;

    @Mock
    private WorkProcess process;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Test
    void startWork() {
        given(workProcessFactory.createForDeconstruction(gameData, PROCESS_ID, DECONSTRUCTION_ID, LOCATION)).willReturn(List.of(process));
        given(process.toModel()).willReturn(processModel);
        given(gameData.getProcesses()).willReturn(processes);

        underTest.startWork(progressDiff, gameData, PROCESS_ID, DECONSTRUCTION_ID, LOCATION);

        then(progressDiff).should().save(processModel);
        then(processes).should().add(process);
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