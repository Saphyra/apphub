package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CitizenToModelConverterTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String NAME = "name";
    private static final int MORALE = 253;
    private static final int SATIETY = 345;
    private static final UUID GAME_ID = UUID.randomUUID();

    @Mock
    private SkillToModelConverter skillToModelConverter;

    @InjectMocks
    private CitizenToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Skill skill;

    @Mock
    private SkillModel skillModel;

    @Test
    public void convertDeep() {
        Citizen citizen = Citizen.builder()
            .citizenId(CITIZEN_ID)
            .location(LOCATION)
            .locationType(LocationType.PLANET)
            .name(NAME)
            .morale(MORALE)
            .satiety(SATIETY)
            .skills(CollectionUtils.singleValueMap(SkillType.AIMING, skill))
            .build();

        given(game.getGameId()).willReturn(GAME_ID);
        given(skillToModelConverter.convert(any(), eq(game))).willReturn(Arrays.asList(skillModel));

        List<GameItem> result = underTest.convertDeep(Arrays.asList(citizen), game);

        CitizenModel expected = new CitizenModel();
        expected.setId(CITIZEN_ID);
        expected.setGameId(GAME_ID);
        expected.setType(GameItemType.CITIZEN);
        expected.setLocation(LOCATION);
        expected.setLocationType(LocationType.PLANET.name());
        expected.setName(NAME);
        expected.setSatiety(SATIETY);
        expected.setMorale(MORALE);

        assertThat(result).containsExactlyInAnyOrder(skillModel, expected);
    }
}