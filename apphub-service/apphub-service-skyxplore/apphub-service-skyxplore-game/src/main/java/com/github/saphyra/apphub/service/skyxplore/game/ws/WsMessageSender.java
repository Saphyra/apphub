package com.github.saphyra.apphub.service.skyxplore.game.ws;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.PlanetStorageResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.QueueResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SurfaceResponse;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPage;
import com.github.saphyra.apphub.service.skyxplore.game.domain.OpenedPageType;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class WsMessageSender {
    private static final List<OpenedPageType> PLANET_PAGE_TYPE_GROUP = ImmutableList.of(OpenedPageType.PLANET, OpenedPageType.PLANET_POPULATION_OVERVIEW);

    private final GameDao gameDao;
    private final MessageSenderProxy messageSenderProxy;

    public void planetQueueItemModified(UUID userId, UUID planetId, QueueResponse queueResponse) {
        if (isNull(queueResponse)) {
            return;
        }
        sendIfProperPageIsOpened(userId, WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED, PLANET_PAGE_TYPE_GROUP, planetId, queueResponse);
    }

    public void planetQueueItemDeleted(UUID userId, UUID planetId, UUID itemId) {
        sendIfProperPageIsOpened(userId, WebSocketEventName.SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED, PLANET_PAGE_TYPE_GROUP, planetId, itemId);
    }

    public void planetSurfaceModified(UUID userId, UUID planetId, SurfaceResponse surfaceResponse) {
        sendIfProperPageIsOpened(userId, WebSocketEventName.SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED, PLANET_PAGE_TYPE_GROUP, planetId, surfaceResponse);
    }

    public void planetStorageModified(UUID userId, UUID planetId, PlanetStorageResponse storage) {
        sendIfProperPageIsOpened(userId, WebSocketEventName.SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED, PLANET_PAGE_TYPE_GROUP, planetId, storage);
    }

    public void planetCitizenModified(UUID userId, UUID planetId, CitizenResponse citizen) {
        sendIfProperPageIsOpened(userId, WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED, OpenedPageType.PLANET_POPULATION_OVERVIEW, planetId, citizen);
    }

    private void sendIfProperPageIsOpened(UUID userId, WebSocketEventName eventName, OpenedPageType requiredPageType, UUID pageId, Object payload) {
        sendIfProperPageIsOpened(userId, eventName, List.of(requiredPageType), pageId, payload);
    }

    private void sendIfProperPageIsOpened(UUID userId, WebSocketEventName eventName, List<OpenedPageType> requiredPageTypes, UUID pageId, Object payload) {
        OpenedPage openedPage = gameDao.findByUserIdValidated(userId)
            .getPlayers()
            .get(userId)
            .getOpenedPage();
        log.debug("{} - RequiredTypes: {}, pageId: {}", openedPage, requiredPageTypes, pageId);

        if (requiredPageTypes.contains(openedPage.getPageType())) {
            if (isNull(pageId) || pageId.equals(openedPage.getPageId())) {
                sendMessage(userId, eventName, payload);
            }
        }
    }

    private void sendMessage(UUID recipient, WebSocketEventName eventName, Object payload) {
        sendMessage(List.of(recipient), eventName, payload);
    }

    private void sendMessage(List<UUID> recipients, WebSocketEventName eventName, Object payload) {
        WebSocketEvent webSocketEvent = WebSocketEvent.builder()
            .eventName(eventName)
            .payload(payload)
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(recipients)
            .event(webSocketEvent)
            .build();
        messageSenderProxy.sendToGame(message);
    }
}
