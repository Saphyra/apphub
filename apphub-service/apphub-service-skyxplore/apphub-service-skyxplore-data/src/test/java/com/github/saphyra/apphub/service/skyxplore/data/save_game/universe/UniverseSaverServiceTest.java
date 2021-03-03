package com.github.saphyra.apphub.service.skyxplore.data.save_game.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
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
public class UniverseSaverServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private UniverseDao universeDao;

    @Mock
    private UniverseModelValidator universeModelValidator;

    @InjectMocks
    private UniverseSaverService underTest;

    @Mock
    private UniverseModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(universeDao).deleteById(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.UNIVERSE);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(universeModelValidator).validate(model);
        verify(universeDao).saveAll(Arrays.asList(model));
    }
}