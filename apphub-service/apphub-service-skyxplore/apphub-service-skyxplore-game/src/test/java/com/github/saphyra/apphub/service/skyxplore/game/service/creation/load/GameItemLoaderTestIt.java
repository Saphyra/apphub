package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlayerModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GameItemLoaderTestIt {
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private GameDataProxy gameDataProxy;

    private final ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(new ObjectMapper());

    private GameItemLoader underTest;

    @Before
    public void setUp() {
        underTest = new GameItemLoader(objectMapperWrapper, gameDataProxy);
    }

    @Test
    public void loadChildren() {
        PlayerModel playerModel = new PlayerModel();
        playerModel.setGameId(PARENT);

        given(gameDataProxy.loadChildren(PARENT, GameItemType.PLAYER)).willReturn(objectMapperWrapper.writeValueAsString(Arrays.asList(playerModel)));

        List<PlayerModel> result = underTest.loadChildren(PARENT, GameItemType.PLAYER, PlayerModel[].class);

        assertThat(result).containsExactly(playerModel);
    }
}