package com.github.saphyra.apphub.api.platform.message_sender.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WebSocketEventName {
    PING("ping"),

    SKYXPLORE_LOBBY_CHAT_SEND_MESSAGE("chat-send-message"),
    SKYXPLORE_LOBBY_JOIN_TO_LOBBY("join-to-lobby"),
    SKYXPLORE_LOBBY_EXIT_FROM_LOBBY("exit-from-lobby"),
    SKYXPLORE_LOBBY_SET_READINESS("set-readiness"),
    SKYXPLORE_LOBBY_CHANGE_ALLIANCE("change-alliance"),
    SKYXPLORE_LOBBY_GAME_SETTINGS_CHANGED("game-settings-changed"),

    SKYXPLORE_LOBBY_GAME_CREATION_INITIATED("game-creation-initiated"),
    SKYXPLORE_LOBBY_GAME_LOADED("game-loaded"),

    SKYXPLORE_GAME_LOADED("game-loaded"),
    SKYXPLORE_GAME_CHAT_SEND_MESSAGE("skyxplore-game-chat-send-message"),
    SKYXPLORE_GAME_USER_JOINED("skyxplore-game-user-joined"),
    SKYXPLORE_GAME_USER_LEFT("skyxplore-game-user-left"),

    SKYXPLORE_MAIN_MENU_INVITATION("invitation");

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
