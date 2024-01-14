package com.github.saphyra.apphub.service.skyxplore.game.ws.planet;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketSessionWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SkyXploreGamePlanetWebSocketHandlerTest {
    private static final String SESSION_ID = "session-id";
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final String FIELD_OPENED_PLANET_IDS = "openedPlanetIds";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FIELD_SESSIONS = "sessions";

    @Spy
    private final UuidConverter uuidConverter = new UuidConverter();

    @Mock
    private GameDao gameDao;

    @Mock
    private WebSocketHandlerContext context;

    @InjectMocks
    private SkyXploreGamePlanetWebSocketHandler underTest;

    @Mock
    private Game game;

    @Mock
    private WebSocketSession session;

    @Mock
    private Principal principal;

    @Test
    void getEndpoint() {
        assertThat(underTest.getEndpoint()).isEqualTo(Endpoints.WS_CONNECTION_SKYXPLORE_GAME_PLANET);
    }

    @Test
    void afterDisconnection() throws IllegalAccessException {
        Map<String, UUID> openedPlanetIds = CollectionUtils.singleValueMap(SESSION_ID, PLANET_ID);
        FieldUtils.writeField(underTest, FIELD_OPENED_PLANET_IDS, openedPlanetIds, true);

        underTest.afterDisconnection(USER_ID, SESSION_ID);

        assertThat(openedPlanetIds).isEmpty();
    }

    @Test
    void handleMessage() throws IllegalAccessException {
        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_GAME_PLANET_OPENED)
            .payload(PLANET_ID)
            .build();

        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));

        underTest.handleMessage(USER_ID, event, SESSION_ID);

        //noinspection unchecked
        assertThat((Map<String, UUID>) FieldUtils.readField(underTest, FIELD_OPENED_PLANET_IDS, true)).containsEntry(SESSION_ID, PLANET_ID);
    }

    @Test
    void getConnectedUsers() throws IllegalAccessException {
        Map<String, UUID> openedPlanetIds = CollectionUtils.singleValueMap(SESSION_ID, PLANET_ID);
        FieldUtils.writeField(underTest, FIELD_OPENED_PLANET_IDS, openedPlanetIds, true);

        Map<String, WebSocketSessionWrapper> sessions = CollectionUtils.singleValueMap(SESSION_ID, new WebSocketSessionWrapper(session, null));
        FieldUtils.writeField(underTest, FIELD_SESSIONS, sessions, true);

        given(session.getPrincipal()).willReturn(principal);
        String userIdString = uuidConverter.convertDomain(USER_ID);
        given(principal.getName()).willReturn(userIdString);
        given(context.getUuidConverter()).willReturn(uuidConverter);

        assertThat(underTest.getConnectedUsers()).containsExactly(WsSessionPlanetIdMapping.builder().sessionId(SESSION_ID).userId(USER_ID).planetId(PLANET_ID).build());
    }
}