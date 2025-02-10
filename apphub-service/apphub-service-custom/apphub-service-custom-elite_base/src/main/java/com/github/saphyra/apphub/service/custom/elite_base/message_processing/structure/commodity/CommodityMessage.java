package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.commodity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.Economy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class CommodityMessage {
    private LocalDateTime timestamp;
    private Long marketId;
    private EdCommodity[] commodities;
    private Economy[] economies = new Economy[0];
    private String[] prohibited;
    private String stationName;
    private String stationType;
    private String systemName;

    //Unused
    @JsonProperty("horizons")
    private Boolean horizons;

    @JsonProperty("odyssey")
    private Boolean odyssey;

    private String carrierDockingAccess;
}
