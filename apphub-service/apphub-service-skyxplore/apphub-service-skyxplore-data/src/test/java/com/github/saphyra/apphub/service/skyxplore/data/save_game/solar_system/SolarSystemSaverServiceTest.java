package com.github.saphyra.apphub.service.skyxplore.data.save_game.solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SolarSystemModel;
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
public class SolarSystemSaverServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SolarSystemDao solarSystemDao;

    @Mock
    private SolarSystemModelValidator solarSystemModelValidator;

    @InjectMocks
    private SolarSystemSaverService underTest;

    @Mock
    private SolarSystemModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(solarSystemDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.SOLAR_SYSTEM);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(solarSystemModelValidator).validate(model);
        verify(solarSystemDao).saveAll(Arrays.asList(model));
    }
}