package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCache;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.TickCacheItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AssignCitizenServiceTest {
    private static final int WORK_POINTS_PER_TICK = 100;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    @Mock
    private TickCache tickCache;

    private AssignCitizenService underTest;

    @Mock
    private Citizen citizen;

    @Mock
    private TickCacheItem tickCacheItem;

    @Before
    public void setUp() {
        underTest = new AssignCitizenService(tickCache, WORK_POINTS_PER_TICK);
    }

    @Test
    public void assignCitizen() {
        Map<UUID, Assignment> assignments = new HashMap<>();
        given(tickCache.get(GAME_ID)).willReturn(tickCacheItem);
        given(tickCacheItem.getCitizenAssignments()).willReturn(assignments);

        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        Assignment result = underTest.assignCitizen(GAME_ID, citizen, LOCATION);

        assertThat(assignments).containsEntry(CITIZEN_ID, result);

        assertThat(result.getCitizen()).isEqualTo(citizen);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getWorkPointsLeft()).isEqualTo(WORK_POINTS_PER_TICK);
    }
}