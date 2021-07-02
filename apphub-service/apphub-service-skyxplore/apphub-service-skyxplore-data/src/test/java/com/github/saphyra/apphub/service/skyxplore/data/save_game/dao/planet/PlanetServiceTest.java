package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
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
public class PlanetServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private PlanetDao planetDao;

    @Mock
    private PlanetModelValidator planetModelValidator;

    @InjectMocks
    private PlanetService underTest;

    @Mock
    private PlanetModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(planetDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.PLANET);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(planetModelValidator).validate(model);
        verify(planetDao).saveAll(Arrays.asList(model));
    }
}