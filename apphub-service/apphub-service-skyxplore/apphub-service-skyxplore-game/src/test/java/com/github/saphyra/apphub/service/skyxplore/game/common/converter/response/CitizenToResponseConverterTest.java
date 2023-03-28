package com.github.saphyra.apphub.service.skyxplore.game.common.converter.response;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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
    private SkillResponse skillResponse;

    @Mock
    private GameData gameData;

    @Test
    public void convert() {
        given(skillToResponseConverter.getSkills(gameData, CITIZEN_ID)).willReturn(CollectionUtils.singleValueMap(SKILL_TYPE, skillResponse));
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(citizen.getName()).willReturn(CITIZEN_NAME);
        given(citizen.getMorale()).willReturn(MORALE);
        given(citizen.getSatiety()).willReturn(SATIETY);

        CitizenResponse result = underTest.convert(gameData, citizen);

        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getName()).isEqualTo(CITIZEN_NAME);
        assertThat(result.getMorale()).isEqualTo(MORALE);
        assertThat(result.getSatiety()).isEqualTo(SATIETY);
        assertThat(result.getSkills()).containsEntry(SKILL_TYPE, skillResponse);
    }
}