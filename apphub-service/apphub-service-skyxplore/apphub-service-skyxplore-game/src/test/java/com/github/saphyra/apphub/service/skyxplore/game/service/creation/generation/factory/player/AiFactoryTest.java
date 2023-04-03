package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AiFactoryTest {
    @Mock
    private PlayerFactory playerFactory;

    @InjectMocks
    private AiFactory underTest;

    @Mock
    private Player player;

    @Mock
    private SkyXploreGameCreationRequest request;

    @Mock
    private AiPlayer aiPlayer;

    @Test
    void generateAis() {
        given(request.getAis()).willReturn(List.of(aiPlayer));
        given(playerFactory.createAi(aiPlayer)).willReturn(player);

        List<Player> result = underTest.generateAis(request);

        assertThat(result).containsExactly(player);
    }
}