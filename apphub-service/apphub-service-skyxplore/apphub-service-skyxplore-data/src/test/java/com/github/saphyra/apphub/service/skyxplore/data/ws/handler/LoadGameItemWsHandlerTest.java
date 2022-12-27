package com.github.saphyra.apphub.service.skyxplore.data.ws.handler;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadGameItemRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.LoadGameItemService;
import com.github.saphyra.apphub.service.skyxplore.data.ws.LoadGameWebSocketHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoadGameItemWsHandlerTest {
    private static final Object PAYLOAD = "payload";
    private static final UUID ID = UUID.randomUUID();
    private static final UUID REQUEST_ID = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private LoadGameItemService loadGameItemService;

    @InjectMocks
    private LoadGameItemWsHandler underTest;

    @Mock
    private LoadGameWebSocketHandler webSocketHandler;

    @Mock
    private WebSocketSession webSocketSession;

    @Mock
    private SkyXploreWsEvent event;

    @Mock
    private LoadGameItemRequest request;

    @Mock
    private GameItem gameItem;

    @Test
    public void canHandle() {
        assertThat(underTest.canHandle(SkyXploreWsEventName.LOAD_GAME_ITEM)).isTrue();
    }

    @Test
    public void handle() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(objectMapperWrapper.convertValue(PAYLOAD, LoadGameItemRequest.class)).willReturn(request);
        given(request.getId()).willReturn(ID);
        given(request.getType()).willReturn(GameItemType.PRODUCTION_ORDER);
        given(loadGameItemService.loadGameItem(ID, GameItemType.PRODUCTION_ORDER)).willReturn(gameItem);
        given(event.getId()).willReturn(REQUEST_ID);

        underTest.handle(webSocketHandler, webSocketSession, event);

        ArgumentCaptor<SkyXploreWsEvent> argumentCaptor = ArgumentCaptor.forClass(SkyXploreWsEvent.class);
        verify(webSocketHandler).sendEvent(eq(webSocketSession), argumentCaptor.capture());
        SkyXploreWsEvent response = argumentCaptor.getValue();
        assertThat(response.getEventName()).isEqualTo(SkyXploreWsEventName.LOAD_GAME_ITEM);
        assertThat(response.getId()).isEqualTo(REQUEST_ID);
        assertThat(response.getPayload()).isEqualTo(gameItem);
    }
}