package com.github.saphyra.apphub.integration.structure.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingChatWsMessageForGame {
    private String room;
    private String message;
}
