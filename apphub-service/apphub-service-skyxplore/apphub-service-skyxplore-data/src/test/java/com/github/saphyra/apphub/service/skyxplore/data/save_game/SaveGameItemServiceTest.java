package com.github.saphyra.apphub.service.skyxplore.data.save_game;


import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
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
    private UniverseModel universeModel;

    @BeforeEach
    public void setUp() {
        given(gameItemService.getType()).willReturn(GameItemType.UNIVERSE);

        underTest = new SaveGameItemService(
            Arrays.asList(gameItemService),
            objectMapperWrapper,
            errorReporterService
        );
    }

    @Test
    public void error() {
        given(objectMapperWrapper.convertValue(universeModel, GameItem.class)).willReturn(universeModel);
        given(universeModel.getType()).willReturn(GameItemType.UNIVERSE);
        given(objectMapperWrapper.convertValue(universeModel, UniverseModel.class)).willThrow(new RuntimeException());

        underTest.save(Arrays.asList(universeModel));

        verify(errorReporterService).report(any(), any());
    }


    @Test
    public void saveGameData() {
        given(objectMapperWrapper.convertValue(universeModel, GameItem.class)).willReturn(universeModel);
        given(universeModel.getType()).willReturn(GameItemType.UNIVERSE);
        given(objectMapperWrapper.convertValue(universeModel, UniverseModel.class)).willReturn(universeModel);

        underTest.save(Arrays.asList(universeModel));

        verify(gameItemService).save(Arrays.asList(universeModel));
    }
}