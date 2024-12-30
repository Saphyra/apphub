package com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_capi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class FcMaterialsCapiMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("CarrierID")
    private String carrierId;

    @JsonProperty("Items")
    private FcMaterialCapiItems items;

    @JsonProperty("MarketID")
    private Long marketId;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;
}
