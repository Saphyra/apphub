package com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_capi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class FcMaterialsCapiMessage {
    private LocalDateTime timestamp;

    @JsonProperty("CarrierID")
    private String carrierId;

    @JsonProperty("Items")
    private FcMaterialCapiItems items;

    @JsonProperty("MarketID")
    private Long marketId;

    //Unused
    private String event;
    private Boolean horizons;
    private Boolean odyssey;
}
