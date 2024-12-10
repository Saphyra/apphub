package com.github.saphyra.apphub.service.skyxplore.game.message_sender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UpdateItem {
    private String key;
    private Object value;
}
