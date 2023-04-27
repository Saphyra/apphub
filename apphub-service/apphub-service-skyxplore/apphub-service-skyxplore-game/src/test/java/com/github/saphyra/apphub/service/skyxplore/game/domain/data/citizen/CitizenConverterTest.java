package com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CitizenConverterTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Integer MORALE = 32;
    private static final Integer SATIETY = 4;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String SKILL = "skill";

    @Mock
    private SkillConverter skillConverter;

    @InjectMocks
    private CitizenConverter underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SkillResponse skillResponse;

    @Test
    void toModel() {
        Citizen citizen = Citizen.builder()
            .citizenId(CITIZEN_ID)
            .location(LOCATION)
            .name(NAME)
            .morale(MORALE)
            .satiety(SATIETY)
            .build();

        CitizenModel result = underTest.toModel(GAME_ID, citizen);

        assertThat(result.getId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CITIZEN);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getMorale()).isEqualTo(MORALE);
        assertThat(result.getSatiety()).isEqualTo(SATIETY);
    }

    @Test
    void toResponse() {
        Citizen citizen = Citizen.builder()
            .citizenId(CITIZEN_ID)
            .location(LOCATION)
            .name(NAME)
            .morale(MORALE)
            .satiety(SATIETY)
            .build();

        given(skillConverter.toResponse(gameData, CITIZEN_ID)).willReturn(Map.of(SKILL, skillResponse));

        CitizenResponse result = underTest.toResponse(gameData, citizen);

        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getMorale()).isEqualTo(MORALE);
        assertThat(result.getSatiety()).isEqualTo(SATIETY);
        assertThat(result.getSkills()).containsEntry(SKILL, skillResponse);
    }
}