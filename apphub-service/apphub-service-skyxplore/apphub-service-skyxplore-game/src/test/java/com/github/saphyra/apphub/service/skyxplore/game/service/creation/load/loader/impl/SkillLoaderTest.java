package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skills;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SkillLoaderTest {
    private static final UUID SKILL_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer LEVEL = 4326;
    private static final Integer EXPERIENCE = 64578;
    private static final Integer NEXT_LEVEL = 365;

    @Mock
    private GameItemLoader gameItemLoader;

    @InjectMocks
    private SkillLoader underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Skills skills;

    @Mock
    private Skill skill;

    @Mock
    private SkillModel model;

    @Test
    void getGameItemType() {
        assertThat(underTest.getGameItemType()).isEqualTo(GameItemType.SKILL);
    }

    @Test
    void getArrayClass() {
        assertThat(underTest.getArrayClass()).isEqualTo(SkillModel[].class);
    }

    @Test
    void addToGameData() {
        given(gameData.getSkills()).willReturn(skills);

        underTest.addToGameData(gameData, List.of(skill));

        verify(skills).addAll(List.of(skill));
    }

    @Test
    void convert() {
        given(model.getId()).willReturn(SKILL_ID);
        given(model.getCitizenId()).willReturn(CITIZEN_ID);
        given(model.getSkillType()).willReturn(SkillType.BUILDING.name());
        given(model.getLevel()).willReturn(LEVEL);
        given(model.getExperience()).willReturn(EXPERIENCE);
        given(model.getNextLevel()).willReturn(NEXT_LEVEL);

        Skill result = underTest.convert(model);

        assertThat(result.getSkillId()).isEqualTo(SKILL_ID);
        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getSkillType()).isEqualTo(SkillType.BUILDING);
        assertThat(result.getLevel()).isEqualTo(LEVEL);
        assertThat(result.getExperience()).isEqualTo(EXPERIENCE);
        assertThat(result.getNextLevel()).isEqualTo(NEXT_LEVEL);
    }
}