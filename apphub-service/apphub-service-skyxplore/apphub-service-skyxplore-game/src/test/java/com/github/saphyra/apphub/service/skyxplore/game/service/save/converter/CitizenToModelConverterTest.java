package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CitizenToModelConverterTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Integer MORALE = 34;
    private static final Integer SATIETY = 346;
    private static final UUID GAME_ID = UUID.randomUUID();

    private final CitizenToModelConverter underTest = new CitizenToModelConverter();

    @Test
    void convert() {
        Citizen citizen = Citizen.builder()
            .citizenId(CITIZEN_ID)
            .location(LOCATION)
            .name(NAME)
            .morale(MORALE)
            .satiety(SATIETY)
            .build();

        CitizenModel result = underTest.convert(GAME_ID, citizen);

        assertThat(result.getId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CITIZEN);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getMorale()).isEqualTo(MORALE);
        assertThat(result.getSatiety()).isEqualTo(SATIETY);
    }
}