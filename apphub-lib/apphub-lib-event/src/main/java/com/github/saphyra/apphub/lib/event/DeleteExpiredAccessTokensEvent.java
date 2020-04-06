package com.github.saphyra.apphub.lib.event;

import lombok.Data;

@Data
public class DeleteExpiredAccessTokensEvent {
    public static final String EVENT_NAME = "delete-expired-access-tokens";
}
