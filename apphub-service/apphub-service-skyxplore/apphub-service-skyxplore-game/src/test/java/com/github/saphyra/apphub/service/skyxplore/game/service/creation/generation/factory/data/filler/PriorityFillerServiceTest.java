package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PriorityFillerServiceTest {
    private static final UUID PRIORITY_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private PriorityFillerService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Priorities priorities;

    @Test
    void fillPriorities() {
        given(gameData.getPlanets()).willReturn(CollectionUtils.singleValueMap(UUID.randomUUID(), planet, new Planets()));
        given(idGenerator.randomUuid()).willReturn(PRIORITY_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(gameData.getPriorities()).willReturn(priorities);

        underTest.fillPriorities(gameData);

        ArgumentCaptor<Priority> argumentCaptor = ArgumentCaptor.forClass(Priority.class);
        verify(priorities, times(PriorityType.values().length)).add(argumentCaptor.capture());

        argumentCaptor.getAllValues()
            .forEach(priority -> {
                assertThat(priority.getPriorityId()).isEqualTo(PRIORITY_ID);
                assertThat(priority.getLocation()).isEqualTo(PLANET_ID);
                assertThat(priority.getType()).isNotNull();
                assertThat(priority.getValue()).isEqualTo(GameConstants.DEFAULT_PRIORITY);
            });
    }
}