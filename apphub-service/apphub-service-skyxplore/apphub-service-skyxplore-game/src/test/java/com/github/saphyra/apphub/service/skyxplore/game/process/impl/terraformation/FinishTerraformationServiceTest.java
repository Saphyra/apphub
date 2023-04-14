package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.AllocationRemovalService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FinishTerraformationServiceTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private AllocationRemovalService allocationRemovalService;

    @InjectMocks
    private FinishTerraformationService underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Construction terraformation;

    @Mock
    private Surface surface;

    @Mock
    private Constructions constructions;

    @Mock
    private Surfaces surfaces;

    @Test
    public void finishTerraformation() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(PLANET_ID, planet, new Planets()));
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);

        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(terraformation.getData()).willReturn(SurfaceType.DESERT.name());
        given(planet.getOwner()).willReturn(USER_ID);
        given(terraformation.getExternalReference()).willReturn(SURFACE_ID);

        underTest.finishTerraformation(syncCache, gameData, PLANET_ID, terraformation);

        verify(allocationRemovalService).removeAllocationsAndReservations(syncCache, gameData, PLANET_ID, USER_ID, CONSTRUCTION_ID);
        verify(surface).setSurfaceType(SurfaceType.DESERT);
        verify(constructions).deleteByConstructionId(CONSTRUCTION_ID);

        verify(syncCache).terraformationFinished(USER_ID, PLANET_ID, terraformation, surface);
    }
}