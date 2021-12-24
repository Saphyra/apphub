package com.github.saphyra.apphub.service.skyxplore.game.service.planet.surface.building.construction;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ConstructionFactoryTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();
    private static final Integer REQUIRED_WORK_POINTS = 4567;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private ConstructionFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(CONSTRUCTION_ID);

        Construction result = underTest.create(BUILDING_ID, REQUIRED_WORK_POINTS);

        assertThat(result.getConstructionId()).isEqualTo(CONSTRUCTION_ID);
        assertThat(result.getLocation()).isEqualTo(BUILDING_ID);
        assertThat(result.getLocationType()).isEqualTo(LocationType.BUILDING);
        assertThat(result.getRequiredWorkPoints()).isEqualTo(REQUIRED_WORK_POINTS);
        assertThat(result.getCurrentWorkPoints()).isEqualTo(0);
        assertThat(result.getPriority()).isEqualTo(GameConstants.DEFAULT_PRIORITY);
    }
}