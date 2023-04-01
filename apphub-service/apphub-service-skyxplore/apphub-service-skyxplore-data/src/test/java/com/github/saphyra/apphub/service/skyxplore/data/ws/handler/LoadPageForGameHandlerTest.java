package com.github.saphyra.apphub.service.skyxplore.data.ws.handler;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadPageForGameRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.LoadGameItemService;
import com.github.saphyra.apphub.service.skyxplore.data.ws.LoadGameWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoadPageForGameHandlerTest {
    private static final Object PAYLOAD = "payload";
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final Integer PAGE = 36;
    private static final UUID EVENT_ID = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private LoadGameItemService loadGameItemService;

    @InjectMocks
    private LoadPageForGameHandler underTest;

    @Mock
    private LoadGameWebSocketHandler loadGameWebSocketHandler;

    @Mock
    private WebSocketSession session;

    @Mock
    private SkyXploreWsEvent event;

    @Mock
    private GameItem gameItem;

    @Test
    void canHandle() {
        assertThat(underTest.canHandle(SkyXploreWsEventName.LOAD_PAGE_FOR_GAME)).isTrue();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void handle() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, LoadPageForGameRequest.class)).willReturn(new LoadPageForGameRequest(
            GAME_ID,
            GameItemType.CONSTRUCTION,
            PAGE
        ));

        List items = List.of(gameItem);
        given(loadGameItemService.loadPageForGameItems(GAME_ID, PAGE, GameItemType.CONSTRUCTION)).willReturn(items);
        given(event.getId()).willReturn(EVENT_ID);

        underTest.handle(loadGameWebSocketHandler, session, event);

        ArgumentCaptor<SkyXploreWsEvent> argumentCaptor = ArgumentCaptor.forClass(SkyXploreWsEvent.class);
        verify(loadGameWebSocketHandler).sendEvent(eq(session), argumentCaptor.capture());
        SkyXploreWsEvent sentEvent = argumentCaptor.getValue();
        assertThat(sentEvent.getId()).isEqualTo(EVENT_ID);
        assertThat(sentEvent.getEventName()).isEqualTo(SkyXploreWsEventName.LOAD_PAGE_FOR_GAME);
        assertThat(sentEvent.getPayload()).isEqualTo(items);
    }
}