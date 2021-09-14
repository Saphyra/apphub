package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.home_planet.HomePlanetSetupService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player.AiFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.player.PlayerFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.universe.UniverseFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameFactoryTest {
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final String ALLIANCE_NAME = "alliance-name";
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID HOST = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID AI_PLAYER_ID = UUID.randomUUID();

    @Mock
    private AllianceFactory allianceFactory;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private UniverseFactory universeFactory;

    @Mock
    private ChatFactory chatFactory;

    @Mock
    private HomePlanetSetupService homePlanetSetupService;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PlayerFactory playerFactory;

    @Mock
    private AiFactory aiFactory;

    @InjectMocks
    private GameFactory underTest;

    @Mock
    private Universe universe;

    @Mock
    private Coordinate coordinate;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Player player;

    @Mock
    private Player aiPlayer;

    @Mock
    private Alliance alliance;

    @Mock
    private Chat chat;

    @Test
    public void create() {
        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .build();
        Map<UUID, UUID> members = CollectionUtils.singleValueMap(USER_ID, ALLIANCE_ID);
        Map<UUID, String> alliances = CollectionUtils.singleValueMap(ALLIANCE_ID, ALLIANCE_NAME);
        SkyXploreGameCreationRequest request = SkyXploreGameCreationRequest.builder()
            .members(members)
            .alliances(alliances)
            .settings(settings)
            .host(HOST)
            .gameName(GAME_NAME)
            .build();

        given(idGenerator.randomUuid()).willReturn(GAME_ID);
        given(aiPlayer.getUserId()).willReturn(AI_PLAYER_ID);

        Map<UUID, Player> players = CollectionUtils.singleValueMap(USER_ID, player);
        given(playerFactory.create(members)).willReturn(players);
        given(aiFactory.generateAis(request, players.values())).willReturn(Arrays.asList(aiPlayer));
        Map<UUID, Alliance> allianceMap = CollectionUtils.singleValueMap(ALLIANCE_ID, alliance);
        given(allianceFactory.create(alliances, members, players)).willReturn(allianceMap);

        given(universeFactory.create(GAME_ID, 2, settings)).willReturn(universe);
        given(universe.getSystems()).willReturn(CollectionUtils.singleValueMap(coordinate, solarSystem));

        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(chatFactory.create(members)).willReturn(chat);

        Game result = underTest.create(request);

        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getHost()).isEqualTo(HOST);
        assertThat(result.getPlayers()).containsEntry(USER_ID, player)
            .containsEntry(AI_PLAYER_ID, aiPlayer);
        assertThat(result.getAlliances()).containsEntry(ALLIANCE_ID, alliance);
        assertThat(result.getUniverse()).isEqualTo(universe);
        assertThat(result.getChat()).isEqualTo(chat);
        assertThat(result.getGameName()).isEqualTo(GAME_NAME);
        assertThat(result.getLastPlayed()).isEqualTo(CURRENT_DATE);

        verify(homePlanetSetupService).setUpHomePlanet(player, allianceMap.values(), CollectionUtils.singleValueMap(coordinate, solarSystem));
    }
}