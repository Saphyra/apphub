package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.shipyard.outfitting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
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
