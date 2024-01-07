package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructionProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @Mock
    private WorkProcessFactory workProcessFactory;

    @InjectMocks
    private DeconstructionProcessHelper underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private WorkProcess workProcess;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Processes processes;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Test
    void startWork() {
        given(workProcessFactory.createForDeconstruction(gameData, PROCESS_ID, DECONSTRUCTION_ID, LOCATION)).willReturn(List.of(workProcess));
        given(gameData.getProcesses()).willReturn(processes);
        given(workProcess.toModel()).willReturn(processModel);

        underTest.startWork(syncCache, gameData, PROCESS_ID, LOCATION, DECONSTRUCTION_ID);

        verify(processes).add(workProcess);
        verify(syncCache).saveGameItem(processModel);
    }

    @Test
    void finishDeconstruction() {
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionId(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_ID);
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(building.getBuildingId()).willReturn(BUILDING_ID);

        underTest.finishDeconstruction(syncCache, gameData, DECONSTRUCTION_ID);

        verify(buildings).remove(building);
        then(deconstructions).should().remove(deconstruction);
        then(syncCache).should().deleteGameItem(DECONSTRUCTION_ID, GameItemType.DECONSTRUCTION);
        then(syncCache).should().deleteGameItem(BUILDING_ID, GameItemType.BUILDING);
    }
}