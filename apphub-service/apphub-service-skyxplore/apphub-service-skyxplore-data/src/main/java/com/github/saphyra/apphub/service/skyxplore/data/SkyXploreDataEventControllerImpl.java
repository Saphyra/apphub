package com.github.saphyra.apphub.service.skyxplore.data;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.api.skyxplore.data.server.SkyXploreDataEventController;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import com.github.saphyra.apphub.service.skyxplore.data.save_game.GameCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SkyXploreDataEventControllerImpl implements SkyXploreDataEventController {
    private final GameCleanupService gameCleanupService;
    private final List<DeleteByUserIdDao> deleteByUserIdDaos;

    @Override
    public void deleteGamesMarkedForDeletion() {
        log.info("Deleting games marked for deletion...");
        gameCleanupService.deleteMarkedGames();
    }

    @Override
    @Transactional
    public void deleteAccountEvent(SendEventRequest<DeleteAccountEvent> request) {
        UUID userId = request.getPayload().getUserId();
        log.info("Processing DeleteAccountEvent for uid {}", userId);
        deleteByUserIdDaos.forEach(deleteByUserIdDao -> deleteByUserIdDao.deleteByUserId(userId));
    }
}
