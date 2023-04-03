package com.github.saphyra.apphub.api.platform.message_sender.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WebSocketEventName {
    //Platform
    PING("ping"),
    REDIRECT("redirect"),

    //AdminPanel
    ADMIN_PANEL_MONITORING_MEMORY_STATUS("admin-panel-monitoring-memory-status"),
    ADMIN_PANEL_ERROR_REPORT("admin-panel-error-report"),

    //SkyXplore MainMenu
    SKYXPLORE_MAIN_MENU_INVITATION("invitation"),
    SKYXPLORE_MAIN_MENU_CANCEL_INVITATION("skyxplore-main-menu-cancel-invitation"),
    SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_ACCEPTED("skyxplore-main-menu-friend-request-accepted"),
    SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_SENT("skyxplore-main-menu-friend-request-sent"),
    SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED("skyxplore-main-menu-friend-request-deleted"),
    SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED("skyxplore-main-menu-friendship-deleted"),

    //SkyXplore Lobby
    SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE("chat-send-message"),
    SKYXPLORE_LOBBY_JOIN_TO_LOBBY("join-to-lobby"),
    SKYXPLORE_LOBBY_EXIT_FROM_LOBBY("exit-from-lobby"),
    SKYXPLORE_LOBBY_SET_READINESS("set-readiness"),
    SKYXPLORE_LOBBY_CHANGE_ALLIANCE("change-alliance"),
    SKYXPLORE_LOBBY_GAME_SETTINGS_MODIFIED("skyxplore-lobby-game-settings-modified"),
    SKYXPLORE_LOBBY_USER_ONLINE("skyxplore-lobby-user-online"),
    SKYXPLORE_LOBBY_USER_OFFLINE("skyxplore-lobby-user-offline"),
    SKYXPLORE_LOBBY_AI_REMOVED("skyxplore-lobby-ai-removed"),
    SKYXPLORE_LOBBY_AI_MODIFIED("skyxplore-lobby-ai-modified"),

    SKYXPLORE_LOBBY_GAME_CREATION_INITIATED("game-creation-initiated"),
    SKYXPLORE_LOBBY_GAME_LOADED("game-loaded"),

    //SkyXplore GamePlatform
    SKYXPLORE_GAME_PAGE_OPENED("skyxplore-game-page-opened"),
    SKYXPLORE_GAME_CHAT_SEND_MESSAGE("skyxplore-game-chat-send-message"),
    SKYXPLORE_GAME_USER_JOINED("skyxplore-game-user-joined"),
    SKYXPLORE_GAME_USER_LEFT("skyxplore-game-user-left"),
    SKYXPLORE_GAME_CHAT_ROOM_CREATED("skyxplore-game-chat-room-created"),
    SKYXPLORE_GAME_PAUSED("skyxplore-game-paused"),

    //SkyXplore GameEvent
    SKYXPLORE_GAME_PLANET_QUEUE_ITEM_MODIFIED("skyxplore-game-planet-queue-item-modified"),
    SKYXPLORE_GAME_PLANET_QUEUE_ITEM_DELETED("skyxplore-game-planet-queue-item-deleted"),
    SKYXPLORE_GAME_PLANET_SURFACE_MODIFIED("skyxplore-game-planet-surface-modified"),
    SKYXPLORE_GAME_PLANET_STORAGE_MODIFIED("skyxplore-game-planet-storage-modified"),
    SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED("skyxplore-game-planet-citizen-modified"),
    SKYXPLORE_GAME_PLANET_BUILDING_DETAILS_MODIFIED("skyxplore-game-planet-building-details-modified"),
    ;

    private final String eventName;

    @JsonValue
    public String getEventName() {
        return eventName;
    }

    @JsonCreator
    public static WebSocketEventName forValues(String value) {
        for (WebSocketEventName eventName : values()) {
            if (eventName.getEventName().equals(value)) {
                return eventName;
            }
        }

        return null;
    }
}
