package com.github.saphyra.apphub.lib.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshAccessTokenExpirationEvent {
    public static final String EVENT_NAME = "refresh-access-token-expiration-event";

    private UUID accessTokenId;
}
