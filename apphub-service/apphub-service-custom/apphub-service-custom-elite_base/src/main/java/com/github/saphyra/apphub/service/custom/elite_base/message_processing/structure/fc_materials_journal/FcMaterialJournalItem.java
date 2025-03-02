package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fc_materials_journal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class FcMaterialJournalItem {
    @JsonProperty("Demand")
    private Integer demand;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Price")
    private Integer price;

    @JsonProperty("Stock")
    private Integer stock;

    private Long id;
}
