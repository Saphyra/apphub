package com.github.saphyra.apphub.api.platform.message_sender.model;

import com.github.saphyra.apphub.lib.config.Endpoints;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessageGroup {
    SKYXPLORE_MAIN_MENU(Endpoints.CONNECTION_SKYXPLORE_MAIN_MENU);

    private final String topic;
}
