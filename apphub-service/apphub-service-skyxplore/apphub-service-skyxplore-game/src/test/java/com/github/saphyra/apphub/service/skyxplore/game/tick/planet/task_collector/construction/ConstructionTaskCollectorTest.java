package com.github.saphyra.apphub.service.skyxplore.game.tick.planet.task_collector.construction;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.tick.TickTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ConstructionTaskCollectorTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer PRIORITY = 25;
    private static final Coordinate COORDINATE_1 = new Coordinate(0, 1);
    private static final Coordinate COORDINATE_2 = new Coordinate(0, 2);
    private static final Coordinate COORDINATE_3 = new Coordinate(0, 3);

    @Mock
    private ConstructionTickTaskFactory constructionTickTaskFactory;

    @InjectMocks
    private ConstructionTaskCollector underTest;

    @Mock
    private Surface surfaceWithoutBuilding;

    @Mock
    private Surface surfaceWithoutConstruction;

    @Mock
    private Surface surfaceWithConstruction;

    @Mock
    private Building buildingWithoutConstruction;

    @Mock
    private Building buildingWIthConstruction;

    @Mock
    private Construction construction;

    @Mock
    private Planet planet;

    @Mock
    private ConstructionTickTask tickTask;

    @Test
    public void getTasks() {
        given(planet.getPriorities()).willReturn(CollectionUtils.singleValueMap(PriorityType.CONSTRUCTION, PRIORITY));
        given(planet.getSurfaces()).willReturn(new SurfaceMap(CollectionUtils.toMap(
            new BiWrapper<>(COORDINATE_1, surfaceWithConstruction),
            new BiWrapper<>(COORDINATE_2, surfaceWithoutConstruction),
            new BiWrapper<>(COORDINATE_3, surfaceWithoutBuilding)
        )));

        given(surfaceWithConstruction.getBuilding()).willReturn(buildingWIthConstruction);
        given(surfaceWithoutConstruction.getBuilding()).willReturn(buildingWithoutConstruction);

        given(buildingWIthConstruction.getConstruction()).willReturn(construction);

        given(constructionTickTaskFactory.create(GAME_ID, planet, surfaceWithConstruction, PRIORITY)).willReturn(tickTask);

        List<TickTask> result = underTest.getTasks(GAME_ID, planet);

        assertThat(result).containsExactly(tickTask);
    }
}