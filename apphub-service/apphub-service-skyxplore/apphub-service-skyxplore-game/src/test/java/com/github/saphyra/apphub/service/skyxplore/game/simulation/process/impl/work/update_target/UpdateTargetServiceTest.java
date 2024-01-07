package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.WorkProcessType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateTargetServiceTest {
    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final int COMPLETED_WORK_POINTS = 345;

    @Mock
    private ConstructionUpdateService constructionUpdateService;

    @Mock
    private TerraformationUpdateService terraformationUpdateService;

    @Mock
    private DeconstructionUpdateService deconstructionUpdateService;

    @InjectMocks
    private UpdateTargetService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Test
    void updateTarget_construction() {
        underTest.updateTarget(syncCache, gameData, WorkProcessType.CONSTRUCTION, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(constructionUpdateService).updateConstruction(syncCache, gameData, TARGET_ID, COMPLETED_WORK_POINTS);
    }

    @Test
    void updateTarget_deconstruction() {
        underTest.updateTarget(syncCache, gameData, WorkProcessType.DECONSTRUCTION, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(deconstructionUpdateService).updateDeconstruction(syncCache, gameData, TARGET_ID, COMPLETED_WORK_POINTS);
    }

    @Test
    void updateTarget_terraformation() {
        underTest.updateTarget(syncCache, gameData, WorkProcessType.TERRAFORMATION, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(terraformationUpdateService).updateTerraformation(syncCache, gameData, TARGET_ID, COMPLETED_WORK_POINTS);
    }
}