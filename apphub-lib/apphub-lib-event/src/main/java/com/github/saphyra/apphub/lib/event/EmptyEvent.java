package com.github.saphyra.apphub.lib.event;

public class EmptyEvent {
    public static final String DELETE_EXPIRED_ACCESS_TOKENS_EVENT_NAME = "delete-expired-access-tokens";
    public static final String SKYXPLORE_LOBBY_CLEANUP_EVENT_NAME = "skyxplore-lobby-cleanup";
    public static final String SKYXPLORE_GAME_CLEANUP_EVENT_NAME = "skyxplore-game-cleanup";
    public static final String SKYXPLORE_GAME_DELETION_EVENT_NAME = "skyxplore-game-deletion";
    public static final String WEB_SOCKET_SEND_PING_EVENT = "web-socket-send-ping-event";
    public static final String WEB_SOCKET_CONNECTION_CLEANUP_EVENT = "web-socket-connection-cleanup";
    public static final String TRIGGER_ACCOUNT_DELETION = "user-data-trigger-deletion";
    public static final String TRIGGER_REVOKE_EXPIRED_BANS = "user-data-trigger-revoke-expired-bans";
    public static final String ADMIN_PANEL_TRIGGER_MEMORY_STATUS_UPDATE = "admin-panel-trigger-memory-status-update";
    public static final String STORAGE_CLEAN_UP_STORED_FILES = "storage-clean-up-stored-files";
    public static final String ACCESS_TOKENS_INVALIDATED = "access-tokens-invalidated";
    public static final String ELITE_BASE_PROCESS_MESSAGES = "elite-base-process-messages";
    public static final String ELITE_BASE_RESET_UNHANDLED_MESSAGES = "elite-base-reset-unhandled-messages";
    public static final String ELITE_BASE_DELETE_EXPIRED_MESSAGES = "elite-base-delete-expired-messages";

    public static final String MIGRATION_ELITE_BASE_RESET_MESSAGE_STATUS_ERROR = "ELITE_BASE_RESET_MESSAGE_STATUS_ERROR";
}
