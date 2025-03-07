package com.github.saphyra.apphub.lib.common_domain;

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
    SKYXPLORE_MAIN_MENU_INVITATION("skyxplore-main-menu-invitation"),
    SKYXPLORE_MAIN_MENU_CANCEL_INVITATION("skyxplore-main-menu-cancel-invitation"),
    SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_ACCEPTED("skyxplore-main-menu-friend-request-accepted"),
    SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_SENT("skyxplore-main-menu-friend-request-sent"),
    SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_DELETED("skyxplore-main-menu-friend-request-deleted"),
    SKYXPLORE_MAIN_MENU_FRIENDSHIP_DELETED("skyxplore-main-menu-friendship-deleted"),

    //SkyXplore Lobby
    SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE("skyxplore-lobby-chat-message"),
    SKYXPLORE_LOBBY_JOIN_TO_LOBBY("skyxplore-join-to-lobby"),
    SKYXPLORE_LOBBY_EXIT("skyxplore-lobby-exit"),
    SKYXPLORE_LOBBY_SET_READINESS("skyxplore-lobby-set-readiness"),
    SKYXPLORE_LOBBY_SETTINGS_MODIFIED("skyxplore-lobby-settings-modified"),
    SKYXPLORE_LOBBY_USER_ONLINE("skyxplore-lobby-user-online"),
    SKYXPLORE_LOBBY_USER_OFFLINE("skyxplore-lobby-user-offline"),
    SKYXPLORE_LOBBY_AI_REMOVED("skyxplore-lobby-ai-removed"),
    SKYXPLORE_LOBBY_AI_MODIFIED("skyxplore-lobby-ai-modified"),
    SKYXPLORE_LOBBY_PLAYER_MODIFIED("skyxplore-lobby-player-modified"),
    SKYXPLORE_LOBBY_ALLIANCE_CREATED("skyxplore-lobby-alliance-created"),
    SKYXPLORE_LOBBY_PLAYER_CONNECTED("skyxplore-lobby-player-connected"),
    SKYXPLORE_LOBBY_PLAYER_DISCONNECTED("skyxplore-lobby-player-disconnected"),
    SKYXPLORE_LOBBY_GAME_CREATION_INITIATED("skyxplore-lobby-game-creation-initiated"),
    SKYXPLORE_LOBBY_GAME_LOADED("skyxplore-lobby-game-loaded"),

    //SkyXplore Game Main
    SKYXPLORE_GAME_CHAT_SEND_MESSAGE("skyxplore-game-chat-send-message"),
    SKYXPLORE_GAME_USER_JOINED("skyxplore-game-user-joined"),
    SKYXPLORE_GAME_USER_LEFT("skyxplore-game-user-left"),
    SKYXPLORE_GAME_CHAT_ROOM_CREATED("skyxplore-game-chat-room-created"),
    SKYXPLORE_GAME_PAUSED("skyxplore-game-paused"),
    SKYXPLORE_GAME_PLAYER_DISCONNECTED("skyxplore-game-player-disconnected"),
    SKYXPLORE_GAME_PLAYER_RECONNECTED("skyxplore-game-player-reconnected"),

    //SkyXplore Game Planet
    SKYXPLORE_GAME_PLANET_OPENED("skyxplore-game-planet-opened"),
    SKYXPLORE_GAME_CONSTRUCTION_AREA_OPENED("skyxplore-game-construction-area-opened"),
    SKYXPLORE_GAME_PLANET_MODIFIED("skyxplore-game-planet-modified"),
    SKYXPLORE_GAME_CONSTRUCTION_AREA_MODIFIED("skyxplore-game-construction-area-modified"),

    //SkyXplore Game Population
    SKYXPLORE_GAME_POPULATION_OPENED("skyxplore-game-population-opened"),
    SKYXPLORE_GAME_POPULATION_MODIFIED("skyxplore-game-population-modified"),
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
