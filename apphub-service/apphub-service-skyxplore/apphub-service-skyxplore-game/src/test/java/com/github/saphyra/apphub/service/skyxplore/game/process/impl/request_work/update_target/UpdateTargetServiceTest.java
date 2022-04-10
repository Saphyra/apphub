package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.update_target;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpdateTargetServiceTest {
    private static final int COMPLETED_WORK_POINTS = 234;
    private static final UUID TARGET_ID = UUID.randomUUID();

    @Mock
    private ConstructionUpdateService constructionUpdateService;

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
    public void other() {
        underTest.updateTarget(syncCache, RequestWorkProcessType.OTHER, game, planet, null, COMPLETED_WORK_POINTS);

        //No exception thrown
    }
}