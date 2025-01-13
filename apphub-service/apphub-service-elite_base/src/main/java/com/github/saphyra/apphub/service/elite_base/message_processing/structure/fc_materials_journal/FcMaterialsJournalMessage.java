package com.github.saphyra.apphub.service.elite_base.message_processing.structure.fc_materials_journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcMaterialsJournalMessage {
    private String event;
    private LocalDateTime timestamp;

    @JsonProperty("CarrierID")
    private String carrierId;

    @JsonProperty("CarrierName")
    private String carrierName;

    @JsonProperty("Items")
    private FcMaterialJournalItem[] items;

    @JsonProperty("MarketID")
    private Long marketId;

    //Unused
    private Boolean horizons;
    private Boolean odyssey;
}
