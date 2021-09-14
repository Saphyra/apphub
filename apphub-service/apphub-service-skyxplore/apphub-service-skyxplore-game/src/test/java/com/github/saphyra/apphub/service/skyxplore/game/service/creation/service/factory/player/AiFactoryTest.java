package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AiFactoryTest {
    private static final String PLAYER_NAME = "player-name";
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final Integer ALLIANCE_COUNT = 425;

    @Mock
    private PlayerFactory playerFactory;

    @Mock
    private AiCountCalculator aiCountCalculator;

    @Mock
    private AllianceCounter allianceCounter;

    @InjectMocks
    private AiFactory underTest;

    @Mock
    private SkyXploreGameCreationRequest request;

    @Mock
    private SkyXploreGameCreationSettingsRequest settings;

    @Mock
    private Player existingPlayer;

    @Mock
    private Player newPlayer;

    @Test
    public void generateAis() {
        given(existingPlayer.getPlayerName()).willReturn(PLAYER_NAME);
        given(playerFactory.createAi(Arrays.asList(PLAYER_NAME))).willReturn(newPlayer);
        Map<UUID, UUID> members = CollectionUtils.singleValueMap(PLAYER_ID, ALLIANCE_ID);
        given(request.getMembers()).willReturn(members);
        given(request.getSettings()).willReturn(settings);
        given(settings.getAiPresence()).willReturn(AiPresence.COMMON);
        given(allianceCounter.getAllianceCount(members)).willReturn(ALLIANCE_COUNT);
        given(aiCountCalculator.getAiCount(1, AiPresence.COMMON, ALLIANCE_COUNT)).willReturn(1);

        List<Player> result = underTest.generateAis(request, Arrays.asList(existingPlayer));

        assertThat(result).containsExactly(newPlayer);
    }
}