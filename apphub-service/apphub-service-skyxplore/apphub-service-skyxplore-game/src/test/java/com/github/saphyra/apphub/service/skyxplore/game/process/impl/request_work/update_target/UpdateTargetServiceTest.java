package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UpdateTargetServiceTest {
    private static final int COMPLETED_WORK_POINTS = 234;
    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

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

    @Mock
    private Planet planet;

    @Test
    public void updateConstruction() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.CONSTRUCTION, gameData, LOCATION, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(constructionUpdateService).updateConstruction(syncCache, gameData, LOCATION, TARGET_ID, COMPLETED_WORK_POINTS);
    }

    @Test
    public void updateDeconstruction() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.DECONSTRUCTION, gameData, LOCATION, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(deconstructionUpdateService).updateDeconstruction(syncCache, gameData, LOCATION, TARGET_ID, COMPLETED_WORK_POINTS);
    }

    @Test
    public void updateTerraformation() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.TERRAFORMATION, gameData, LOCATION, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(terraformationUpdateService).updateTerraformation(syncCache, gameData, LOCATION, TARGET_ID, COMPLETED_WORK_POINTS);
    }

    @Test
    public void other() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.OTHER, gameData, LOCATION, null, COMPLETED_WORK_POINTS);

        //No exception thrown
    }
}