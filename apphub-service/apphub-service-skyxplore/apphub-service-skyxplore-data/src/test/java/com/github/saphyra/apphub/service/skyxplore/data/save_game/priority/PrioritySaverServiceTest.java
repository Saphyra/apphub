package com.github.saphyra.apphub.service.skyxplore.data.save_game.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PrioritySaverServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    @Mock
    private PriorityDao priorityDao;

    @Mock
    private PriorityModelValidator priorityModelValidator;

    @InjectMocks
    private PrioritySaverService underTest;

    @Mock
    private PriorityModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(priorityDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.PRIORITY);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(priorityModelValidator).validate(model);
        verify(priorityDao).saveAll(Arrays.asList(model));
    }
}