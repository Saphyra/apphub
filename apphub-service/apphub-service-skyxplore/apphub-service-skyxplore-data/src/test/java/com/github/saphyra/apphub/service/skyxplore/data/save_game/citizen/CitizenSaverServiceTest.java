package com.github.saphyra.apphub.service.skyxplore.data.save_game.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
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
public class CitizenSaverServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private CitizenDao citizenDao;

    @Mock
    private CitizenModelValidator citizenModelValidator;

    @InjectMocks
    private CitizenSaverService underTest;

    @Mock
    private CitizenModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(citizenDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.CITIZEN);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(citizenModelValidator).validate(model);
        verify(citizenDao).saveAll(Arrays.asList(model));
    }
}