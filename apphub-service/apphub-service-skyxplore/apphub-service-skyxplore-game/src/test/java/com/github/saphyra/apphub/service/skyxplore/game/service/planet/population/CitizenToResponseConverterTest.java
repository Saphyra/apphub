package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CitizenToResponseConverterTest {
    private static final String SKILL_TYPE = "skill-type";
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String CITIZEN_NAME = "citizen-name";
    private static final Integer MORALE = 12341;
    private static final Integer SATIETY = 341;

    @Mock
    private SkillToResponseConverter skillToResponseConverter;

    @InjectMocks
    private CitizenToResponseConverter underTest;

    @Mock
    private Citizen citizen;

    @Mock
    private Skill skill;

    @Mock
    private SkillResponse skillResponse;

    @Test
    public void convert() {
        given(citizen.getSkills()).willReturn(CollectionUtils.singleValueMap(SkillType.AIMING, skill));
        given(skillToResponseConverter.getSkills(CollectionUtils.singleValueMap(SkillType.AIMING, skill))).willReturn(CollectionUtils.singleValueMap(SKILL_TYPE, skillResponse));
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(citizen.getName()).willReturn(CITIZEN_NAME);
        given(citizen.getMorale()).willReturn(MORALE);
        given(citizen.getSatiety()).willReturn(SATIETY);

        CitizenResponse result = underTest.convert(citizen);

        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getName()).isEqualTo(CITIZEN_NAME);
        assertThat(result.getMorale()).isEqualTo(MORALE);
        assertThat(result.getSatiety()).isEqualTo(SATIETY);
        assertThat(result.getSkills()).containsEntry(SKILL_TYPE, skillResponse);
    }
}