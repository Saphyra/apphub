package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class SkillModel extends GameItem {
    private UUID citizenId;
    private String skillType;
    private int level;
    private int experience;
    private int nextLevel;
}
