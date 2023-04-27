package com.github.saphyra.apphub.service.skyxplore.data.save_game;


import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.GameItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SaveGameItemServiceTest {
    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private GameItemService gameItemService;

    @Mock
    private ErrorReporterService errorReporterService;

    private SaveGameItemService underTest;

    @Mock
    private GameModel gameModel;

    @BeforeEach
    public void setUp() {
        given(gameItemService.getType()).willReturn(GameItemType.GAME);

        underTest = new SaveGameItemService(
            Arrays.asList(gameItemService),
            objectMapperWrapper,
            errorReporterService
        );
    }

    @Test
    public void error() {
        given(objectMapperWrapper.convertValue(gameModel, GameItem.class)).willReturn(gameModel);
        given(gameModel.getType()).willReturn(GameItemType.GAME);
        given(objectMapperWrapper.convertValue(gameModel, GameModel.class)).willThrow(new RuntimeException());

        underTest.save(Arrays.asList(gameModel));

        verify(errorReporterService).report(any(), any());
    }


    @Test
    public void saveGameData() {
        given(objectMapperWrapper.convertValue(gameModel, GameItem.class)).willReturn(gameModel);
        given(gameModel.getType()).willReturn(GameItemType.GAME);
        given(objectMapperWrapper.convertValue(gameModel, GameModel.class)).willReturn(gameModel);

        underTest.save(Arrays.asList(gameModel));

        verify(gameItemService).save(Arrays.asList(gameModel));
    }
}