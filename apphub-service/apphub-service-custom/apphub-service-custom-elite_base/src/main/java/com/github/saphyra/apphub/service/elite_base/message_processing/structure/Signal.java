package com.github.saphyra.apphub.service.elite_base.message_processing.structure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Signal {
    @JsonProperty("Type")
    private String type;

    @JsonProperty("Count")
    private Integer count;
}
