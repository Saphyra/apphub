package com.github.saphyra.apphub.service.skyxplore.game.service.save;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.GameToGameItemListConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameSaverServiceTest {
    @Mock
    private GameToGameItemListConverter converter;

    @Mock
    private GameItemSaverService saverService;
    private final ExecutorServiceBean executorServiceBean = new ExecutorServiceBean(new SleepService());

    private GameSaverService underTest;

    @Mock
    private Game game;

    @Mock
    private GameItem gameItem;

    @Before
    public void setUp() {
        underTest = GameSaverService.builder()
            .converter(converter)
            .saverService(saverService)
            .executorServiceBean(executorServiceBean)
            .build();
    }

    @Test
    public void save() {
        given(converter.convertDeep(game)).willReturn(Arrays.asList(gameItem));

        underTest.save(game);

        verify(saverService).saveAsync(Arrays.asList(gameItem));
    }
}