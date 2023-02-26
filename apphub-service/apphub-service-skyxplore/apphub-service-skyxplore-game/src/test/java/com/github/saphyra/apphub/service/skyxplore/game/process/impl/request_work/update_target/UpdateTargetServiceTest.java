package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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
    private Game game;

    @Mock
    private Planet planet;

    @Test
    public void updateConstruction() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.CONSTRUCTION, game, planet, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(constructionUpdateService).updateConstruction(syncCache, game, planet, TARGET_ID, COMPLETED_WORK_POINTS);
    }

    @Test
    public void updateDeconstruction() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.DECONSTRUCTION, game, planet, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(deconstructionUpdateService).updateDeconstruction(syncCache, game, planet, TARGET_ID, COMPLETED_WORK_POINTS);
    }

    @Test
    public void updateTerraformation() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.TERRAFORMATION, game, planet, TARGET_ID, COMPLETED_WORK_POINTS);

        verify(terraformationUpdateService).updateTerraformation(syncCache, game, planet, TARGET_ID, COMPLETED_WORK_POINTS);
    }

    @Test
    public void other() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.OTHER, game, planet, null, COMPLETED_WORK_POINTS);

        //No exception thrown
    }
}