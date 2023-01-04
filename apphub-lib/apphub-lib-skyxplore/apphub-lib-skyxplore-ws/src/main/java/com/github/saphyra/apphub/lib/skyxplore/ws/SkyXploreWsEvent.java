package com.github.saphyra.apphub.lib.skyxplore.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkyXploreWsEvent {
    private SkyXploreWsEventName eventName;
    private UUID id;
    private Object payload;
}
