package com.github.saphyra.apphub.lib.event;

import com.github.saphyra.apphub.lib.common_util.ForRemoval;

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
    @ForRemoval("notebook-redesign")
    public static final String NOTEBOOK_MIGRATE_CHECKLISTS = "notebook-migrate-checklists";
    @ForRemoval("notebook-redesign")
    public static final String NOTEBOOK_MIGRATE_TABLES = "notebook-migrate-tables";
}
