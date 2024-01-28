package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PlanetUpdateItem {
    private String key;
    private Object value;
}
