package com.github.saphyra.apphub.integration.structure.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SkillResponse {
    private int experience;
    private int level;
    private int nextLevel;
}
