package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CitizenLoaderTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Integer MORALE = 245;
    private static final Integer SATIETY = 351;

    @Mock
    private GameItemLoader gameItemLoader;

    @Mock
    private SkillLoader skillLoader;

    @InjectMocks
    private CitizenLoader underTest;

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private Skill skill;

    @Test
    public void load() {
        given(gameItemLoader.loadChildren(LOCATION, GameItemType.CITIZEN, CitizenModel[].class)).willReturn(Arrays.asList(citizenModel));

        given(citizenModel.getId()).willReturn(CITIZEN_ID);
        given(citizenModel.getLocation()).willReturn(LOCATION);
        given(citizenModel.getLocationType()).willReturn(LocationType.PLANET.name());
        given(citizenModel.getName()).willReturn(NAME);
        given(citizenModel.getMorale()).willReturn(MORALE);
        given(citizenModel.getSatiety()).willReturn(SATIETY);

        given(skillLoader.load(CITIZEN_ID)).willReturn(CollectionUtils.singleValueMap(SkillType.AIMING, skill));

        Map<UUID, Citizen> result = underTest.load(LOCATION);

        assertThat(result).hasSize(1);
        Citizen citizen = result.get(CITIZEN_ID);
        assertThat(citizen.getCitizenId()).isEqualTo(CITIZEN_ID);
        assertThat(citizen.getLocation()).isEqualTo(LOCATION);
        assertThat(citizen.getLocationType()).isEqualTo(LocationType.PLANET);
        assertThat(citizen.getName()).isEqualTo(NAME);
        assertThat(citizen.getMorale()).isEqualTo(MORALE);
        assertThat(citizen.getSatiety()).isEqualTo(SATIETY);
        assertThat(citizen.getSkills()).containsEntry(SkillType.AIMING, skill);
    }
}