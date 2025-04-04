package com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Economy {
    @JsonAlias("Name")
    private String name;

    @JsonAlias("Proportion")
    private Double proportion;
}
