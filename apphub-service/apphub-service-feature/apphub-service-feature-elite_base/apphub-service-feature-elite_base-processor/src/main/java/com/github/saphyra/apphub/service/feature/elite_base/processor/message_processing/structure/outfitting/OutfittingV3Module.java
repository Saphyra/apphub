package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.structure.outfitting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class OutfittingV3Module {
    private Long id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("BuyPrice")
    private Integer buyPrice;
    @JsonProperty("BuyMercCoinPrice")
    private Integer buyMercCoinPrice;
}
