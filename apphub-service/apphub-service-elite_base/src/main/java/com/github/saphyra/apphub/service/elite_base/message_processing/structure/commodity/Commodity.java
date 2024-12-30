package com.github.saphyra.apphub.service.elite_base.message_processing.structure.commodity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Commodity {
    private String name;
    private Integer buyPrice;
    private Integer sellPrice;
    private Integer demand;
    private Integer stock;


    @JsonProperty("meanPrice")
    private Integer averagePrice;

    //Unused
    private Integer demandBracket;
    private Integer stockBracket;

    private String[] statusFlags;
}
