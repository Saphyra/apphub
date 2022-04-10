package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameModel;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.chat.Chat;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Alliance;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.BackgroundProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.background.BackgroundProcesses;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoop;
import com.github.saphyra.apphub.service.skyxplore.game.process.event_loop.EventLoopFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.ChatFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GameLoaderTest {
    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String GAME_NAME = "game-name";
    private static final UUID HOST = UUID.randomUUID();
    private static final UUID ALLIANCE_ID = UUID.randomUUID();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private GameDao gameDao;

    @Mock
    private PlayerLoader playerLoader;

    @Mock
    private AllianceLoader allianceLoader;

    @Mock
    private ChatFactory chatFactory;

    @Mock
    private UniverseLoader universeLoader;

    @Mock
    private GameDataProxy gameDataProxy;

    @Mock
    private MessageSenderProxy messageSenderProxy;

    @Mock
    private EventLoopFactory eventLoopFactory;

    @Mock
    private BackgroundProcessFactory backgroundProcessFactory;

    @Mock
    private ProcessLoader processLoader;

    @InjectMocks
    private GameLoader underTest;

    @Mock
    private GameModel gameModel;

    @Mock
    private Player player;

    @Mock
    private Alliance alliance;

    @Mock
    private Universe universe;

    @Mock
    private Chat chat;

    @Mock
    private EventLoop eventLoop;

    @Mock
    private Process process;

    @Mock
    private BackgroundProcesses backgroundProcesses;

    @Test
    public void load() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        given(gameModel.getId()).willReturn(GAME_ID);
        given(gameModel.getName()).willReturn(GAME_NAME);
        given(gameModel.getHost()).willReturn(HOST);
        given(gameModel.getLastPlayed()).willReturn(CURRENT_DATE);

        Map<UUID, Player> players = CollectionUtils.singleValueMap(USER_ID, player);
        given(playerLoader.load(GAME_ID, Arrays.asList(MEMBER_ID))).willReturn(players);
        given(allianceLoader.load(GAME_ID, players)).willReturn(CollectionUtils.singleValueMap(ALLIANCE_ID, alliance));
        given(universeLoader.load(GAME_ID)).willReturn(universe);
        given(chatFactory.create(players.values())).willReturn(chat);

        given(eventLoopFactory.create()).willReturn(eventLoop);
        given(processLoader.load(any(Game.class))).willReturn(List.of(process));
        given(backgroundProcessFactory.create(any(Game.class))).willReturn(backgroundProcesses);

        underTest.loadGame(gameModel, Arrays.asList(MEMBER_ID));

        verify(gameModel).setLastPlayed(CURRENT_DATE);
        verify(gameDataProxy).saveItem(gameModel);

        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameDao).save(gameArgumentCaptor.capture());
        Game game = gameArgumentCaptor.getValue();
        assertThat(game.getGameId()).isEqualTo(GAME_ID);
        assertThat(game.getGameName()).isEqualTo(GAME_NAME);
        assertThat(game.getHost()).isEqualTo(HOST);
        assertThat(game.getLastPlayed()).isEqualTo(CURRENT_DATE);
        assertThat(game.getPlayers()).containsEntry(USER_ID, player);
        assertThat(game.getAlliances()).containsEntry(ALLIANCE_ID, alliance);
        assertThat(game.getUniverse()).isEqualTo(universe);
        assertThat(game.getChat()).isEqualTo(chat);
        assertThat(game.getEventLoop()).isEqualTo(eventLoop);
        assertThat(game.getBackgroundProcesses()).isEqualTo(backgroundProcesses);
        assertThat(game.getProcesses()).containsExactly(process);

        ArgumentCaptor<WebSocketMessage> messageArgumentCaptor = ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(messageSenderProxy).sendToLobby(messageArgumentCaptor.capture());
        WebSocketMessage message = messageArgumentCaptor.getValue();
        assertThat(message.getRecipients()).containsExactly(MEMBER_ID);
        assertThat(message.getEvent().getEventName()).isEqualTo(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);
    }
}