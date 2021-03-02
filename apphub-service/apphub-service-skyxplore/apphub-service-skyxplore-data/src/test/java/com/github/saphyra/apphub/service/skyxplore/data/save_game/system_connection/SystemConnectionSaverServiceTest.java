package com.github.saphyra.apphub.service.skyxplore.data.save_game.system_connection;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SystemConnectionModel;
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
public class SystemConnectionSaverServiceTest {
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SystemConnectionDao systemConnectionDao;

    @Mock
    private SystemConnectionModelValidator systemConnectionModelValidator;

    @InjectMocks
    private SystemConnectionSaverService underTest;

    @Mock
    private SystemConnectionModel model;

    @Test
    public void deleteByGameId() {
        underTest.deleteByGameId(GAME_ID);

        verify(systemConnectionDao).deleteByGameId(GAME_ID);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(GameItemType.SYSTEM_CONNECTION);
    }

    @Test
    public void save() {
        underTest.save(Arrays.asList(model));

        verify(systemConnectionModelValidator).validate(model);
        verify(systemConnectionDao).saveAll(Arrays.asList(model));
    }
}