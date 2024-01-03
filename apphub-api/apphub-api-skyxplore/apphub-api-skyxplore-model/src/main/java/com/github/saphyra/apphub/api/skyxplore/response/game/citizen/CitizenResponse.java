package com.github.saphyra.apphub.api.skyxplore.response.game.citizen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CitizenResponse {
    private UUID citizenId;
    private String name;
    private Map<CitizenStat, StatResponse> stats;
    private Map<String, SkillResponse> skills;
}
