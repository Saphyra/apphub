package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadChildrenOfGameItemRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadGameItemRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.ws.SkyXploreWsClient;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.ws.WebSocketClientCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GameItemLoaderTest {
    private static final UUID ID = UUID.randomUUID();
    private static final String DATA = "data";
    private static final UUID REQUEST_ID = UUID.randomUUID();

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private WebSocketClientCache wsClientCache;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private GameItemLoader underTest;

    @Mock
    private UniverseModel universeModel;

    @Mock
    private SkyXploreWsClient wsClient;

    @Mock
    private SkyXploreWsEvent wsEvent;

    @Mock
    private GameItem gameItem;

    @Captor
    private ArgumentCaptor<Predicate<SkyXploreWsEvent>> responseArgumentCaptor;

    @BeforeEach
    public void setUp() throws Exception {
        given(wsClientCache.borrowObject()).willReturn(wsClient);
        given(idGenerator.randomUuid()).willReturn(REQUEST_ID);
    }

    @Test
    public void loadItem() {
        given(wsClient.awaitForEvent(any())).willReturn(wsEvent);
        given(wsEvent.getPayload()).willReturn(gameItem);
        given(objectMapperWrapper.convertValue(gameItem, UniverseModel.class)).willReturn(universeModel);

        Optional<UniverseModel> result = underTest.loadItem(ID, GameItemType.UNIVERSE);

        ArgumentCaptor<SkyXploreWsEvent> eventArgumentCaptor = ArgumentCaptor.forClass(SkyXploreWsEvent.class);
        verify(wsClient).send(eventArgumentCaptor.capture());
        SkyXploreWsEvent event = eventArgumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(SkyXploreWsEventName.LOAD_GAME_ITEM);
        assertThat(event.getId()).isEqualTo(REQUEST_ID);
        assertThat(event.getPayload()).isEqualTo(LoadGameItemRequest.builder().id(ID).type(GameItemType.UNIVERSE).build());

        assertThat(result).contains(universeModel);

        verify(wsClient).awaitForEvent(responseArgumentCaptor.capture());
        Predicate<SkyXploreWsEvent> predicate = responseArgumentCaptor.getValue();

        given(wsEvent.getId()).willReturn(REQUEST_ID);
        given(wsEvent.getEventName()).willReturn(SkyXploreWsEventName.LOAD_GAME_ITEM);
        assertThat(predicate.test(wsEvent)).isTrue();
    }

    @Test
    public void loadChildren() {
        given(wsClient.awaitForEvent(any())).willReturn(wsEvent);
        given(wsEvent.getPayload()).willReturn(List.of(gameItem));
        given(objectMapperWrapper.convertValue(List.of(gameItem), UniverseModel[].class)).willReturn(new UniverseModel[]{universeModel});

        List<UniverseModel> result = underTest.loadChildren(ID, GameItemType.UNIVERSE, UniverseModel[].class);

        ArgumentCaptor<SkyXploreWsEvent> eventArgumentCaptor = ArgumentCaptor.forClass(SkyXploreWsEvent.class);
        verify(wsClient).send(eventArgumentCaptor.capture());
        SkyXploreWsEvent event = eventArgumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(SkyXploreWsEventName.LOAD_CHILDREN_OF_GAME_ITEM);
        assertThat(event.getId()).isEqualTo(REQUEST_ID);
        assertThat(event.getPayload()).isEqualTo(LoadChildrenOfGameItemRequest.builder().parent(ID).type(GameItemType.UNIVERSE).build());

        assertThat(result).contains(universeModel);

        verify(wsClient).awaitForEvent(responseArgumentCaptor.capture());
        Predicate<SkyXploreWsEvent> predicate = responseArgumentCaptor.getValue();

        given(wsEvent.getId()).willReturn(REQUEST_ID);
        given(wsEvent.getEventName()).willReturn(SkyXploreWsEventName.LOAD_CHILDREN_OF_GAME_ITEM);
        assertThat(predicate.test(wsEvent)).isTrue();
    }
}