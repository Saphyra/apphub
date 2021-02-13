package com.github.saphyra.apphub.api.skyxplore.model.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SkillModel extends GameItem {
    private UUID citizenId;
    private String skillType;
    private Integer level;
    private Integer experience;
    private Integer nextLevel;
}
