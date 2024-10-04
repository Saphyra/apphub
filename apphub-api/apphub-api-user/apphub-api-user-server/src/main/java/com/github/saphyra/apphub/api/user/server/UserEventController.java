package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.GenericEndpoints;
import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import com.github.saphyra.apphub.lib.event.DeleteAccountEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserEventController {
    /**
     * Removing all the user data from all the database tables
     * Triggered by user-service
     */
    @PostMapping(GenericEndpoints.EVENT_DELETE_ACCOUNT)
    void deleteAccountEvent(@RequestBody SendEventRequest<DeleteAccountEvent> request);

    /**
     * Delete users marked for deletion, and if they can be deleted, triggers the user removal by firing a DeleteAccountEvent
     * Triggered by scheduler-service
     */
    @PostMapping(UserEndpoints.EVENT_TRIGGER_ACCOUNT_DELETION)
    void triggerAccountDeletion();

    /**
     * Removing the bans, when they are expired.
     * Triggered by scheduler-service
     */
    @PostMapping(UserEndpoints.EVENT_TRIGGER_REVOKE_EXPIRED_BANS)
    void triggerRevokeExpiredBans();
}
