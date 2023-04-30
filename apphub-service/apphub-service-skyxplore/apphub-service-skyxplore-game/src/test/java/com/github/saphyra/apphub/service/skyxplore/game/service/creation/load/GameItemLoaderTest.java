package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.skyxplore.ws.LoadPageForGameRequest;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEvent;
import com.github.saphyra.apphub.lib.skyxplore.ws.SkyXploreWsEventName;
import com.github.saphyra.apphub.service.skyxplore.game.common.ws.SkyXploreWsClient;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.ws.WebSocketClientCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameItemLoaderTest {
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final int PAGE = 34;
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final Object PAYLOAD = "payload";

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @Mock
    private WebSocketClientCache wsClientCache;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private GameItemLoader underTest;

    @Mock
    private SkyXploreWsClient wsClient;

    @Mock
    private SkyXploreWsEvent response;

    @Mock
    private PlanetModel planetModel;

    @Captor
    private ArgumentCaptor<Predicate<SkyXploreWsEvent>> predicateArgumentCaptor;

    @Test
    void loadPageForGame() throws Exception {
        given(wsClientCache.borrowObject()).willReturn(wsClient);
        given(idGenerator.randomUuid()).willReturn(EVENT_ID);
        given(wsClient.awaitForEvent(any())).willReturn(response);
        given(response.getPayload()).willReturn(PAYLOAD);
        PlanetModel[] convertedPayload = {planetModel};
        given(objectMapperWrapper.convertValue(PAYLOAD, PlanetModel[].class)).willReturn(convertedPayload);

        List<PlanetModel> result = underTest.loadPageForGame(GAME_ID, PAGE, GameItemType.PLANET, PlanetModel[].class);

        ArgumentCaptor<SkyXploreWsEvent> argumentCaptor = ArgumentCaptor.forClass(SkyXploreWsEvent.class);
        verify(wsClient).send(argumentCaptor.capture());
        SkyXploreWsEvent event = argumentCaptor.getValue();
        assertThat(event.getEventName()).isEqualTo(SkyXploreWsEventName.LOAD_PAGE_FOR_GAME);
        assertThat(event.getId()).isEqualTo(EVENT_ID);
        LoadPageForGameRequest request = (LoadPageForGameRequest) event.getPayload();
        assertThat(request.getGameId()).isEqualTo(GAME_ID);
        assertThat(request.getPage()).isEqualTo(PAGE);
        assertThat(request.getType()).isEqualTo(GameItemType.PLANET);

        assertThat(result).containsExactly(planetModel);

        verify(wsClient).awaitForEvent(predicateArgumentCaptor.capture());
        Predicate<SkyXploreWsEvent> predicate = predicateArgumentCaptor.getValue();
        given(response.getId())
            .willReturn(UUID.randomUUID())
            .willReturn(EVENT_ID);
        given(response.getEventName())
            .willReturn(SkyXploreWsEventName.LOAD_PAGE_FOR_GAME)
            .willReturn(null)
            .willReturn(SkyXploreWsEventName.LOAD_PAGE_FOR_GAME);

        assertThat(predicate.test(response)).isFalse(); //EventId does not match
        assertThat(predicate.test(response)).isFalse(); //EventName does not match
        assertThat(predicate.test(response)).isTrue();
    }
}