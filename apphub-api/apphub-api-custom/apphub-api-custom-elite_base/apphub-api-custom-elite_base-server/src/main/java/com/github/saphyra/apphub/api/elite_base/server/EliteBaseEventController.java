package com.github.saphyra.apphub.api.elite_base.server;

import com.github.saphyra.apphub.lib.config.common.endpoints.EliteBaseEndpoints;
import org.springframework.web.bind.annotation.PostMapping;

public interface EliteBaseEventController {
    @PostMapping(EliteBaseEndpoints.EVENT_PROCESS_MESSAGES)
    void processMessages();

    @PostMapping(EliteBaseEndpoints.EVENT_RESET_UNHANDLED_MESSAGES)
    void resetUnhandled();

    @PostMapping(EliteBaseEndpoints.EVENT_DELETE_EXPIRED_MESSAGES)
    void deleteExpiredMessages();

    @PostMapping(EliteBaseEndpoints.EVENT_MIGRATION_ELITE_BASE_RESET_MESSAGE_STATUS_ERROR)
    void resetError();

    @PostMapping(EliteBaseEndpoints.EVENT_MIGRATION_ELITE_BASE_ORPHANED_RECORD_CLEANUP)
    void cleanupOrphanedRecords();
}
