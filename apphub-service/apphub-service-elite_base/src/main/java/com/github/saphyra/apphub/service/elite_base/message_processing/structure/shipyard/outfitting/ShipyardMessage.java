package com.github.saphyra.apphub.service.elite_base.message_processing.structure.shipyard.outfitting;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ShipyardMessage {
    private LocalDateTime timestamp;
    private Long marketId;
    private String[] ships;
    private String stationName;
    private String systemName;

    //Unused
    private Boolean allowCobraMkIV;
    private Boolean horizons;
    private Boolean odyssey;
}
