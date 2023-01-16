package com.github.saphyra.apphub.service.skyxplore.data.ws.handler;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadChildrenOfGameItemRequest;
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
public class LoadChildrenOfGameItemWsHandlerTest {
    private static final Object PAYLOAD = "payload";
    private static final UUID PARENT = UUID.randomUUID();
    private static final UUID REQUEST_ID = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private LoadGameItemService loadGameItemService;

    @InjectMocks
    private LoadChildrenOfGameItemWsHandler underTest;

    @Mock
    private LoadGameWebSocketHandler webSocketHandler;

    @Mock
    private WebSocketSession webSocketSession;

    @Mock
    private SkyXploreWsEvent event;

    @Mock
    private LoadChildrenOfGameItemRequest request;

    @Mock
    private GameItem gameItem;

    @Test
    public void canHandle() {
        assertThat(underTest.canHandle(SkyXploreWsEventName.LOAD_CHILDREN_OF_GAME_ITEM)).isTrue();
    }

    @Test
    public void handle() {
        given(event.getPayload()).willReturn(PAYLOAD);
        given(event.getId()).willReturn(REQUEST_ID);
        given(objectMapperWrapper.convertValue(PAYLOAD, LoadChildrenOfGameItemRequest.class)).willReturn(request);
        given(request.getParent()).willReturn(PARENT);
        given(request.getType()).willReturn(GameItemType.PRODUCTION_ORDER);
        List gameItems = List.of(gameItem);
        given(loadGameItemService.loadChildrenOfGameItem(PARENT, GameItemType.PRODUCTION_ORDER)).willReturn(gameItems);

        underTest.handle(webSocketHandler, webSocketSession, event);

        ArgumentCaptor<SkyXploreWsEvent> argumentCaptor = ArgumentCaptor.forClass(SkyXploreWsEvent.class);
        verify(webSocketHandler).sendEvent(eq(webSocketSession), argumentCaptor.capture());
        SkyXploreWsEvent response = argumentCaptor.getValue();
        assertThat(response.getId()).isEqualTo(REQUEST_ID);
        assertThat(response.getEventName()).isEqualTo(SkyXploreWsEventName.LOAD_CHILDREN_OF_GAME_ITEM);
        assertThat(response.getPayload()).isEqualTo(gameItems);
    }
}