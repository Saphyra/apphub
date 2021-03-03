package com.github.saphyra.apphub.service.skyxplore.data.save_game;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreDataGameControllerImplTest {
    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private GameItemSaver gameItemSaver;

    private SkyXploreDataGameControllerImpl underTest;

    @Mock
    private UniverseModel universeModel;

    @Before
    public void setUp() {
        given(gameItemSaver.getType()).willReturn(GameItemType.UNIVERSE);

        underTest = new SkyXploreDataGameControllerImpl(
            Arrays.asList(gameItemSaver),
            objectMapperWrapper
        );
    }

    @Test
    public void saveGameData() {
        given(objectMapperWrapper.convertValue(universeModel, GameItem.class)).willReturn(universeModel);
        given(universeModel.getType()).willReturn(GameItemType.UNIVERSE);
        given(objectMapperWrapper.convertValue(universeModel, UniverseModel.class)).willReturn(universeModel);

        underTest.saveGameData(Arrays.asList(universeModel));

        verify(gameItemSaver).save(Arrays.asList(universeModel));
    }
}