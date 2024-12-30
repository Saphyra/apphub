package com.github.saphyra.apphub.service.elite_base.message_processing.structure.outfitting;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class OutfittingMessage {
    private LocalDateTime timestamp;
    private Long marketId;
    private String[] modules;
    private String stationName;
    private String systemName;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;
}
