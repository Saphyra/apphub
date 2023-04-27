package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work.update_target;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeconstructionUpdateServiceTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final int COMPLETED_WORK_POINTS = 354;
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.randomUUID();

    private final DeconstructionUpdateService underTest = new DeconstructionUpdateService();

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Mock
    private Planet planet;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Test
    void updateDeconstruction() {
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(deconstructions.findByDeconstructionId(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_ID);
        given(gameData.getBuildings()).willReturn(buildings);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(building.getSurfaceId()).willReturn(SURFACE_ID);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(LOCATION, planet, new Planets()));
        given(planet.getOwner()).willReturn(OWNER_ID);

        underTest.updateDeconstruction(syncCache, gameData, LOCATION, DECONSTRUCTION_ID, COMPLETED_WORK_POINTS);

        verify(deconstruction).increaseWorkPoints(COMPLETED_WORK_POINTS);
        verify(syncCache).deconstructionUpdated(OWNER_ID, LOCATION, deconstruction, surface);
    }
}