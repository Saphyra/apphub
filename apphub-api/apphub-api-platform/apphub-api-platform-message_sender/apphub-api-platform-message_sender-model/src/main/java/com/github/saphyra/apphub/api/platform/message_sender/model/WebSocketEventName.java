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
