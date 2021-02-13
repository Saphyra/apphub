package com.github.saphyra.apphub.service.skyxplore.data.save_game.skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(schema = "skyxplore_game", name = "skill")
class SkillEntity {
    @Id
    private String skillId;
    private String gameId;
    private String citizenId;
    private String skillType;
    private int level;
    private int experience;
    private int nextLevel;
}
