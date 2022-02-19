package com.github.saphyra.apphub.integration.structure.skyxplore;

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
    private int morale;
    private int satiety;
    private Map<String, SkillResponse> skills;
}
