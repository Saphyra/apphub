package com.github.saphyra.apphub.service.elite_base.message_processing.structure.commodity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class CommodityMessage {
    @JsonProperty("timestamp")
    private LocalDateTime createdAt;

    private Long marketId;
    private Commodity[] commodities;
    private Economy[] economies;
    private String[] prohibited;
    private String stationName;
    private String stationType;
    private String systemName;
    private String carrierDockingAccess;

    //Unused
    @JsonProperty("horizons")
    private Boolean horizons;

    @JsonProperty("odyssey")
    private Boolean odyssey;
}
