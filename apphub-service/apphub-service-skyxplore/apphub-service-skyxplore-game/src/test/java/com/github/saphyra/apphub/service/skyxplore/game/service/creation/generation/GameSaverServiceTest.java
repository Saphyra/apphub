package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameConverter;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameSaverServiceTest {
    @Mock
    private GameConverter converter;

    @Mock
    private GameDataProxy gameDataProxy;

    @InjectMocks
    private GameSaverService underTest;

    @Mock
    private Game game;

    @Mock
    private GameItem gameItem;

    @Test
    public void save() {
        given(converter.convertDeep(game)).willReturn(Arrays.asList(gameItem));

        underTest.save(game);

        verify(gameDataProxy).saveItems(Arrays.asList(gameItem));
    }
}