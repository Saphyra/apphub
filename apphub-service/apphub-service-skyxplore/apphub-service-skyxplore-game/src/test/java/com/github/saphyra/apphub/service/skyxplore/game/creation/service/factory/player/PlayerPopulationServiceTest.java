package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.player;

import com.github.saphyra.apphub.api.skyxplore.model.game_setting.AiPresence;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PlayerPopulationServiceTest {
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final int PLANET_AMOUNT = 3;
    private static final UUID PLAYER_ID_1 = UUID.randomUUID();
    private static final UUID PLAYER_ID_2 = UUID.randomUUID();
    private static final Integer AI_PRESENCE = 10;

    @Mock
    private GameCreationProperties properties;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private Random random;

    @Mock
    private PlayerFactory playerFactory;

    @InjectMocks
    private PlayerPopulationService underTest;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private GameCreationProperties.PlayerCreationProperties playerCreationProperties;

    @Test
    public void populate() {
        given(properties.getPlayer()).willReturn(playerCreationProperties);
        given(playerCreationProperties.getSpawnChance()).willReturn(CollectionUtils.singleValueMap(AiPresence.COMMON, AI_PRESENCE));

        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .aiPresence(AiPresence.COMMON)
            .build();
        Set<UUID> userIds = new HashSet<>();
        userIds.add(USER_ID_1);

        given(playerFactory.create(USER_ID_1, false, Collections.emptyList())).willReturn(player1);

        given(idGenerator.randomUuid()).willReturn(PLAYER_ID_1);
        given(random.randInt(0, 100)).willReturn(AI_PRESENCE - 1)
            .willReturn(AI_PRESENCE + 1);

        given(playerFactory.create(PLAYER_ID_1, true, Collections.emptyList())).willReturn(player2);

        given(player1.getUserId()).willReturn(USER_ID_1);
        given(player2.getUserId()).willReturn(USER_ID_2);

        Map<UUID, Player> result = underTest.populateGameWithPlayers(userIds, PLANET_AMOUNT, settings);

        assertThat(result).hasSize(2);
        assertThat(result).containsEntry(USER_ID_1, player1);
        assertThat(result).containsEntry(USER_ID_2, player2);
    }

    @Test
    public void populate_addOneAi() {
        given(properties.getPlayer()).willReturn(playerCreationProperties);
        given(playerCreationProperties.getSpawnChance()).willReturn(CollectionUtils.singleValueMap(AiPresence.COMMON, AI_PRESENCE));

        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .aiPresence(AiPresence.COMMON)
            .build();
        Set<UUID> userIds = new HashSet<>();
        userIds.add(USER_ID_1);

        given(playerFactory.create(USER_ID_1, false, Collections.emptyList())).willReturn(player1);

        given(idGenerator.randomUuid()).willReturn(PLAYER_ID_1);
        given(random.randInt(0, 100)).willReturn(AI_PRESENCE + 1);

        given(playerFactory.create(PLAYER_ID_1, true, Collections.emptyList())).willReturn(player2);

        given(player1.getUserId()).willReturn(USER_ID_1);
        given(player2.getUserId()).willReturn(USER_ID_2);

        Map<UUID, Player> result = underTest.populateGameWithPlayers(userIds, PLANET_AMOUNT, settings);

        assertThat(result).hasSize(2);
        assertThat(result).containsEntry(USER_ID_1, player1);
        assertThat(result).containsEntry(USER_ID_2, player2);
    }
}