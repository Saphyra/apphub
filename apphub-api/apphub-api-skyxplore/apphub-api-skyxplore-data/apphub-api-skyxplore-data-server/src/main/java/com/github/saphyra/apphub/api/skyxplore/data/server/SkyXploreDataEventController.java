package com.github.saphyra.apphub.api.skyxplore.data.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SkyXploreDataEventController {
    @PostMapping(GenericSkyXploreEndpoints.EVENT_SKYXPLORE_DELETE_GAMES)
    void deleteGamesMarkedForDeletion();

    @PostMapping(GenericEndpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);
}
