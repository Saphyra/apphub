package com.github.saphyra.apphub.service.skyxplore.game.event;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Player;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.MessageSenderProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class SkyXploreGameAccountDeletedEventController {
    private final GameDao gameDao;
    private final MessageSenderProxy messageSenderProxy;

    @PostMapping(Endpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request) {
        UUID userId = request.getPayload().getUserId();
        log.info("Processing DeleteAccountEvent for uid {}", userId);
        gameDao.findByUserId(userId).ifPresent(game -> processUserDeletion(game, userId));
    }

    private void processUserDeletion(Game game, UUID userId) {
        if (game.getHost().equals(userId)) {
            log.info("{} is the host. Deleting game {}", userId, game.getGameId());
            gameDao.delete(game);
            WebSocketEvent event = WebSocketEvent.builder()
                .eventName(WebSocketEventName.REDIRECT)
                .payload(Endpoints.SKYXPLORE_MAIN_MENU_PAGE)
                .build();

            WebSocketMessage message = WebSocketMessage.builder()
                .recipients(game.getConnectedPlayers())
                .event(event)
                .build();
            messageSenderProxy.sendToGame(message);
        } else {
            log.info("Setting player {} to ai", userId);
            Player player = game.getPlayers().get(userId);
            player.setAi(true);
            player.setConnected(true);
            player.setPlayerName(player.getPlayerName() + " (AI)");
        }
    }
}
