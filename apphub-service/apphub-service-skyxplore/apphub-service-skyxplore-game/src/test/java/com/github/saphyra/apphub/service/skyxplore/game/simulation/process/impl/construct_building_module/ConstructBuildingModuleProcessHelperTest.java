package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorages;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.resource_request.ResourceRequestProcessFactory;
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
class ConstructBuildingModuleProcessHelperTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int REQUIRED_WORK_POINTS = 243;
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private WorkProcessFactory workProcessFactory;

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @Mock
    private ResourceRequestProcessFactory resourceRequestProcessFactory;

    @Mock
    private StoredResourceService storedResourceService;

    @InjectMocks
    private ConstructBuildingModuleProcessHelper underTest;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private GameData gameData;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private Processes processes;

    @Mock
    private WorkProcess process;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Game game;

    @Mock
    private ReservedStorages reservedStorages;

    @Mock
    private ReservedStorage reservedStorage;

    @Test
    void createResourceRequestProcess() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getReservedStorages()).willReturn(reservedStorages);
        given(reservedStorages.getByExternalReference(CONSTRUCTION_ID)).willReturn(List.of(reservedStorage));
        given(reservedStorage.getReservedStorageId()).willReturn(RESERVED_STORAGE_ID);

        underTest.createResourceRequestProcess(game, LOCATION, PROCESS_ID, CONSTRUCTION_ID);

        then(resourceRequestProcessFactory).should().save(game, LOCATION, PROCESS_ID, RESERVED_STORAGE_ID);
    }

    @Test
    void startWork() {
        given(game.getProgressDiff()).willReturn(progressDiff);
        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getLocation()).willReturn(LOCATION);
        given(construction.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);

        underTest.startWork(game, PROCESS_ID, CONSTRUCTION_ID);

        then(storedResourceService).should().useResources(progressDiff, gameData, PROCESS_ID);
        then(workProcessFactory).should().save(game, LOCATION, PROCESS_ID, REQUIRED_WORK_POINTS, SkillType.BUILDING);
    }

    @Test
    void finishConstruction() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(construction);

        underTest.finishConstruction(progressDiff, gameData, CONSTRUCTION_ID);

        then(allocationRemovalService).should().removeAllocationsAndReservations(progressDiff, gameData, CONSTRUCTION_ID);
        then(constructions).should().remove(construction);
        then(progressDiff).should().delete(CONSTRUCTION_ID, GameItemType.CONSTRUCTION);
    }
}