package com.github.saphyra.apphub.service.skyxplore.game.service.planet.priority;

import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PriorityQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    public static final int PRIORITY_VALUE = 2;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private PriorityQueryService underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Test
    public void getPriorities() {
        given(gameDao.findByUserIdValidated(USER_ID)).willReturn(game);
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.getByLocation(PLANET_ID)).willReturn(List.of(priority));
        given(priority.getValue()).willReturn(PRIORITY_VALUE);
        given(priority.getType()).willReturn(PriorityType.CONSTRUCTION);

        Map<String, Integer> result = underTest.getPriorities(USER_ID, PLANET_ID);

        assertThat(result).hasSize(1)
            .containsEntry(PriorityType.CONSTRUCTION.name().toLowerCase(), PRIORITY_VALUE);
    }
}