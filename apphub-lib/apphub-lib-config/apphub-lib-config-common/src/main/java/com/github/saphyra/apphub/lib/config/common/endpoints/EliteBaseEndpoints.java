package com.github.saphyra.apphub.lib.config.common.endpoints;

public class EliteBaseEndpoints {
    public static final String EVENT_PROCESS_MESSAGES = "/event/elite-base/messages/process";
    public static final String EVENT_RESET_UNHANDLED_MESSAGES = "/event/elite-base/messages/unhandled/reset";
    public static final String EVENT_DELETE_EXPIRED_MESSAGES = "/event/elite-base/messages/delete-expired";
    public static final String EVENT_MIGRATION_ELITE_BASE_RESET_MESSAGE_STATUS_ERROR = "/event/elite-base/migration/message/status/error/reset";

    public static final String ELITE_BASE_STAR_SYSTEMS_SEARCH = "/api/elite-base/star-systems/search";
    public static final String ELITE_BASE_NEAREST_MATERIAL_TRADERS = "/api/elite-base/nearest/{starId}/material-traders/{materialType}/{page}";
}
