package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SkyXploreDataEventController {
    @PostMapping(Endpoints.EVENT_SKYXPLORE_DELETE_GAMES)
    void deleteGamesMarkedForDeletion();

    @PostMapping(Endpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);
}
