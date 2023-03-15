package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.BackgroundProcessStarterService;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoopFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.home_planet.HomePlanetSetupService;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player.AiFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.player.PlayerFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.universe.UniverseFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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
    private EventLoopFactory eventLoopFactory;

    @Mock
    private HomePlanetSetupService homePlanetSetupService;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private PlayerFactory playerFactory;

    @Mock
    private AiFactory aiFactory;

    @Mock
    private BackgroundProcessStarterService backgroundProcessStarterService;

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

    @Mock
    private EventLoop eventLoop;

    @Test
    public void create() {
        SkyXploreGameCreationSettingsRequest settings = SkyXploreGameCreationSettingsRequest.builder()
            .build();
        Map<UUID, UUID> members = Map.of(USER_ID, ALLIANCE_ID);
        Map<UUID, String> alliances = Map.of(ALLIANCE_ID, ALLIANCE_NAME);
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
        Map<UUID, Alliance> allianceMap = Map.of(ALLIANCE_ID, alliance);
        given(allianceFactory.create(alliances, members, players)).willReturn(allianceMap);

        given(universeFactory.create(GAME_ID, 2, settings)).willReturn(universe);
        given(universe.getSystems()).willReturn(Map.of(coordinate, solarSystem));

        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);
        given(chatFactory.create(members)).willReturn(chat);
        given(eventLoopFactory.create()).willReturn(eventLoop);

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
        assertThat(result.getEventLoop()).isEqualTo(eventLoop);
        assertThat(result.getMarkedForDeletion()).isFalse();

        verify(homePlanetSetupService).setUpHomePlanet(player, allianceMap.values(), Map.of(coordinate, solarSystem));
        verify(backgroundProcessStarterService).startBackgroundProcesses(result);
    }
}