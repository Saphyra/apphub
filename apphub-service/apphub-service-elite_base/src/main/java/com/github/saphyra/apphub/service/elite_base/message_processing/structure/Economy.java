package com.github.saphyra.apphub.service.elite_base.message_processing.structure;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Economy {
    @JsonAlias("Name")
    private String name;

    @JsonAlias("Proportion")
    private Double proportion;
}
