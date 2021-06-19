package com.github.saphyra.apphub.lib.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PageVisitedEvent {
    public static final String EVENT_NAME = "page-visited-event";

    private UUID accessTokenId;
    private String pageUrl;
}
