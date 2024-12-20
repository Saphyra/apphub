package com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.module.education;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Education {
    private String id;
    private SkillType skill;
    private Range<Integer> levelLimit;
}
