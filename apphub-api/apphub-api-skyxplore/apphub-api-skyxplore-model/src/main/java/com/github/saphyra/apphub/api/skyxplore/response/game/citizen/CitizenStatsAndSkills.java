package com.github.saphyra.apphub.api.skyxplore.response.game.citizen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CitizenStatsAndSkills {
    private List<String> stats;
    private List<String> skills;
}
