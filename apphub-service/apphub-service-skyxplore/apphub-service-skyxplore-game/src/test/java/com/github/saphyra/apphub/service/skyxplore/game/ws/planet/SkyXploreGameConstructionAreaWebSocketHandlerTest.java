package com.github.saphyra.apphub.service.skyxplore.game.ws.planet;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketHandlerContext;
import com.github.saphyra.apphub.lib.web_socket.core.handler.WebSocketSessionWrapper;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionConstructionAreaIdMapping;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SkyXploreGameConstructionAreaWebSocketHandlerTest {
    private static final String SESSION_ID = "session-id";
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String FIELD_OPENED_CONSTRUCTION_AREAS = "openedConstructionAreas";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String FIELD_SESSIONS = "sessions";

    @Spy
    private final UuidConverter uuidConverter = new UuidConverter();

    @Mock
    private WebSocketHandlerContext context;

    @Mock
    private GameDao gameDao;

    @InjectMocks
    private SkyXploreGameConstructionAreaWebSocketHandler underTest;

    @Mock
    private Game game;

    @Mock
    private WebSocketSession session;

    @Mock
    private Principal principal;


    @Test
    void getEndpoint() {
        assertThat(underTest.getEndpoint()).isEqualTo(GenericSkyXploreEndpoints.WS_CONNECTION_SKYXPLORE_GAME_CONSTRUCTION_AREA);
    }

    @Test
    void afterDisconnection() throws IllegalAccessException {
        Map<String, UUID> openedConstructionAreas = CollectionUtils.singleValueMap(SESSION_ID, CONSTRUCTION_AREA_ID);
        FieldUtils.writeField(underTest, FIELD_OPENED_CONSTRUCTION_AREAS, openedConstructionAreas, true);

        underTest.afterDisconnection(USER_ID, SESSION_ID);

        assertThat(openedConstructionAreas).isEmpty();
    }

    @Test
    void handleMessage() throws IllegalAccessException {
        Map<String, UUID> openedConstructionAreas = new HashMap<>();
        FieldUtils.writeField(underTest, FIELD_OPENED_CONSTRUCTION_AREAS, openedConstructionAreas, true);

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_GAME_CONSTRUCTION_AREA_OPENED)
            .payload(CONSTRUCTION_AREA_ID)
            .build();
        given(uuidConverter.convertEntity(CONSTRUCTION_AREA_ID.toString())).willReturn(CONSTRUCTION_AREA_ID);
        given(gameDao.findByUserId(USER_ID)).willReturn(Optional.of(game));

        underTest.handleMessage(USER_ID, event, SESSION_ID);

        assertThat(openedConstructionAreas).containsEntry(SESSION_ID, CONSTRUCTION_AREA_ID);
    }

    @Test
    void getConnectedUsers() throws IllegalAccessException {
        Map<String, UUID> openedConstructionAreas = CollectionUtils.singleValueMap(SESSION_ID, CONSTRUCTION_AREA_ID);
        FieldUtils.writeField(underTest, FIELD_OPENED_CONSTRUCTION_AREAS, openedConstructionAreas, true);

        Map<String, WebSocketSessionWrapper> sessions = CollectionUtils.singleValueMap(SESSION_ID, new WebSocketSessionWrapper(session, null));
        FieldUtils.writeField(underTest, FIELD_SESSIONS, sessions, true);

        given(session.getPrincipal()).willReturn(principal);
        String userIdString = uuidConverter.convertDomain(USER_ID);
        given(principal.getName()).willReturn(userIdString);
        given(context.getUuidConverter()).willReturn(uuidConverter);

        assertThat(underTest.getConnectedUsers()).containsExactly(WsSessionConstructionAreaIdMapping.builder().sessionId(SESSION_ID).userId(USER_ID).constructionAreaId(CONSTRUCTION_AREA_ID).build());
    }
}