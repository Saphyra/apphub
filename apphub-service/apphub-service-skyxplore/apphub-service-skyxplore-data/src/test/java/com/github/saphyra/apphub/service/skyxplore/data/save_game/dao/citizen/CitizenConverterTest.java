package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CitizenConverterTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String NAME = "name";
    private static final Integer MORALE = 42352;
    private static final Integer SATIETY = 398214;
    private static final String CITIZEN_ID_STRING = "citizen-id";
    private static final String GAME_ID_STRING = "game-id";
    private static final String LOCATION_STRING = "location";
    private static final String WEAPON_DATA_ID = "weapon-data-id";
    private static final String MELEE_WEAPON_DATA_ID = "melee-weapon-data-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private CitizenConverter underTest;

    @Test
    public void convertDomain() {
        CitizenModel model = new CitizenModel();
        model.setId(CITIZEN_ID);
        model.setGameId(GAME_ID);
        model.setLocation(LOCATION);
        model.setName(NAME);
        model.setMorale(MORALE);
        model.setSatiety(SATIETY);
        model.setWeaponDataId(WEAPON_DATA_ID);
        model.setMeleeWeaponDataId(MELEE_WEAPON_DATA_ID);

        given(uuidConverter.convertDomain(CITIZEN_ID)).willReturn(CITIZEN_ID_STRING);
        given(uuidConverter.convertDomain(GAME_ID)).willReturn(GAME_ID_STRING);
        given(uuidConverter.convertDomain(LOCATION)).willReturn(LOCATION_STRING);

        CitizenEntity result = underTest.convertDomain(model);

        assertThat(result.getCitizenId()).isEqualTo(CITIZEN_ID_STRING);
        assertThat(result.getGameId()).isEqualTo(GAME_ID_STRING);
        assertThat(result.getLocation()).isEqualTo(LOCATION_STRING);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getMorale()).isEqualTo(MORALE);
        assertThat(result.getSatiety()).isEqualTo(SATIETY);
        assertThat(result.getWeaponDataId()).isEqualTo(WEAPON_DATA_ID);
        assertThat(result.getMeleeWeaponDataId()).isEqualTo(MELEE_WEAPON_DATA_ID);
    }

    @Test
    public void convertEntity() {
        CitizenEntity entity = CitizenEntity.builder()
            .citizenId(CITIZEN_ID_STRING)
            .gameId(GAME_ID_STRING)
            .location(LOCATION_STRING)
            .name(NAME)
            .satiety(SATIETY)
            .morale(MORALE)
            .weaponDataId(WEAPON_DATA_ID)
            .meleeWeaponDataId(MELEE_WEAPON_DATA_ID)
            .build();

        given(uuidConverter.convertEntity(CITIZEN_ID_STRING)).willReturn(CITIZEN_ID);
        given(uuidConverter.convertEntity(GAME_ID_STRING)).willReturn(GAME_ID);
        given(uuidConverter.convertEntity(LOCATION_STRING)).willReturn(LOCATION);

        CitizenModel result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(CITIZEN_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.CITIZEN);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getMorale()).isEqualTo(MORALE);
        assertThat(result.getSatiety()).isEqualTo(SATIETY);
        assertThat(result.getWeaponDataId()).isEqualTo(WEAPON_DATA_ID);
        assertThat(result.getMeleeWeaponDataId()).isEqualTo(MELEE_WEAPON_DATA_ID);
    }
}