package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameItemLoaderTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String DATA = "data";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private GameDataProxy gameDataProxy;

    @InjectMocks
    private GameItemLoader underTest;

    @Mock
    private UniverseModel universeModel;

    @Test
    public void loadItem() {
        given(gameDataProxy.loadItem(ID, GameItemType.UNIVERSE)).willReturn(DATA);
        given(objectMapperWrapper.readValue(DATA, UniverseModel.class)).willReturn(universeModel);

        Optional<UniverseModel> result = underTest.loadItem(ID, GameItemType.UNIVERSE);

        assertThat(result).contains(universeModel);
    }

    @Test
    public void loadChildren() {
        given(gameDataProxy.loadChildren(ID, GameItemType.UNIVERSE)).willReturn(DATA);
        given(objectMapperWrapper.readArrayValue(DATA, UniverseModel[].class)).willReturn(Arrays.asList(universeModel));

        List<UniverseModel> result = underTest.loadChildren(ID, GameItemType.UNIVERSE, UniverseModel[].class);

        assertThat(result).containsExactly(universeModel);
    }
}