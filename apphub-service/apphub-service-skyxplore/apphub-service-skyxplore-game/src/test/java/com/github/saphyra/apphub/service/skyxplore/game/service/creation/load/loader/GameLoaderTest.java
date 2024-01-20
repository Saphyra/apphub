package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.alliance.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.player.Player;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.ChatFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.event_loop.EventLoopFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickSchedulerLauncher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameLoaderTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();
    private static final Integer UNIVERSE_SIZE = 324;
    private static final String LOCALE = "locale";

    @Mock
    private GameDao gameDao;

    @Mock
    private PlayerLoader playerLoader;

    @Mock
    private AllianceLoader allianceLoader;

    @Mock
    private ChatFactory chatFactory;

    @Mock
    private SkyXploreLobbyApiClient lobbyClient;

    @Mock
    private EventLoopFactory eventLoopFactory;

    @Mock
    private GameDataLoader gameDataLoader;

    @Mock
    private ProcessLoader processLoader;

    @Mock
    private TickSchedulerLauncher tickSchedulerLauncher;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private GameLoader underTest;

    @Mock
    private GameModel gameModel;

    @Mock
    private Player player;

    @Mock
    private Alliance alliance;

    @Mock
    private GameData gameData;

    @Mock
    private Chat chat;

    @Mock
    private EventLoop eventLoop;

    @Test
    void loadGame() {
        given(gameModel.getId()).willReturn(GAME_ID);
        Map<UUID, Player> players = Map.of(USER_ID, player);
        given(playerLoader.load(GAME_ID, List.of(USER_ID))).willReturn(players);
        given(gameModel.getName()).willReturn(GAME_NAME);
        given(gameModel.getHost()).willReturn(HOST);
        given(allianceLoader.load(GAME_ID, players)).willReturn(Map.of(ALLIANCE_ID, alliance));
        given(gameModel.getUniverseSize()).willReturn(UNIVERSE_SIZE);
        given(gameDataLoader.load(GAME_ID, UNIVERSE_SIZE)).willReturn(gameData);
        given(chatFactory.create(players.values())).willReturn(chat);
        given(eventLoopFactory.create()).willReturn(eventLoop);
        given(gameModel.getMarkedForDeletion()).willReturn(false);
        given(gameModel.getMarkedForDeletionAt()).willReturn(null);
        given(gameModel.getLastPlayed()).willReturn(CURRENT_TIME);
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.loadGame(gameModel, List.of(USER_ID));

        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameDao).save(gameArgumentCaptor.capture());
        Game game = gameArgumentCaptor.getValue();
        assertThat(game.getGameId()).isEqualTo(GAME_ID);
        assertThat(game.getGameName()).isEqualTo(GAME_NAME);
        assertThat(game.getHost()).isEqualTo(HOST);
        assertThat(game.getLastPlayed()).isEqualTo(CURRENT_TIME);
        assertThat(game.getPlayers()).containsEntry(USER_ID, player);
        assertThat(game.getAlliances()).containsEntry(ALLIANCE_ID, alliance);
        assertThat(game.getData()).isEqualTo(gameData);
        assertThat(game.getChat()).isEqualTo(chat);
        assertThat(game.getEventLoop()).isEqualTo(eventLoop);
        assertThat(game.getMarkedForDeletion()).isFalse();
        assertThat(game.getMarkedForDeletionAt()).isNull();

        verify(processLoader).loadProcesses(game);
        verify(tickSchedulerLauncher).launch(game);

        then(lobbyClient).should().gameLoaded(GAME_ID, LOCALE);
    }
}