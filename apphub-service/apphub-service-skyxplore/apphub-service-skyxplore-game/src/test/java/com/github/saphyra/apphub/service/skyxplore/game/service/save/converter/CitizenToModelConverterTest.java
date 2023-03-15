package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_armor_piece.BodyPart;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_armor_piece.SoldierArmorPiece;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_data.SoldierWeapon;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_energy_shield.SoldierEnergyShield;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CitizenToModelConverterTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String NAME = "name";
    private static final int MORALE = 253;
    private static final int SATIETY = 345;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final String WEAPON_DATA_ID = "weapon-data-id";
    private static final int MAX_HIT_POINTS = 435;
    private static final int CURRENT_HIT_POINTS = 3243;

    @Mock
    private SkillToModelConverter skillToModelConverter;

    @Mock
    private SoldierEnergyShieldToModelConverter energyShieldConverter;

    @Mock
    private SoldierArmorPieceToModelConverter soldierArmorPieceConverter;

    @InjectMocks
    private CitizenToModelConverter underTest;

    @Mock
    private Game game;

    @Mock
    private Skill skill;

    @Mock
    private SkillModel skillModel;

    @Mock
    private SoldierArmorPiece soldierArmorPiece;

    @Mock
    private SoldierEnergyShield energyShield;

    @Mock
    private DurabilityItemModel energyShieldModel;

    @Mock
    private DurabilityItemModel armorModel;

    @Test
    public void convertDeep() {
        ConcurrentHashMap<BodyPart, SoldierArmorPiece> armor = new ConcurrentHashMap<>(CollectionUtils.singleValueMap(BodyPart.CHEST, soldierArmorPiece));
        Citizen citizen = Citizen.builder()
            .citizenId(CITIZEN_ID)
            .location(LOCATION)
            .locationType(LocationType.PLANET)
            .name(NAME)
            .morale(MORALE)
            .satiety(SATIETY)
            .skills(CollectionUtils.singleValueMap(SkillType.AIMING, skill))
            .soldierData(
                SoldierWeapon.builder()
                    .maxHitPoints(MAX_HIT_POINTS)
                    .currentHitPoints(CURRENT_HIT_POINTS)
                    .armor(armor)
                    .energyShield(energyShield)
                    .weaponDataId(WEAPON_DATA_ID)
                    .build()
            )
            .build();

        given(game.getGameId()).willReturn(GAME_ID);
        given(skillToModelConverter.convert(anyCollection(), eq(GAME_ID))).willReturn(Arrays.asList(skillModel));
        given(energyShieldConverter.convert(CITIZEN_ID, GAME_ID, energyShield)).willReturn(energyShieldModel);
        given(soldierArmorPieceConverter.convert(CITIZEN_ID, GAME_ID, armor)).willReturn(List.of(armorModel));

        List<GameItem> result = underTest.convertDeep(Arrays.asList(citizen), game);

        CitizenModel citizenModel = new CitizenModel();
        citizenModel.setId(CITIZEN_ID);
        citizenModel.setGameId(GAME_ID);
        citizenModel.setType(GameItemType.CITIZEN);
        citizenModel.setLocation(LOCATION);
        citizenModel.setLocationType(LocationType.PLANET.name());
        citizenModel.setName(NAME);
        citizenModel.setSatiety(SATIETY);
        citizenModel.setMorale(MORALE);
        citizenModel.setWeaponDataId(WEAPON_DATA_ID);

        DurabilityItemModel soldierModel = new DurabilityItemModel();
        soldierModel.setId(CITIZEN_ID);
        soldierModel.setGameId(GAME_ID);
        soldierModel.setType(GameItemType.DURABILITY_ITEM_MODEL);
        soldierModel.setMaxDurability(MAX_HIT_POINTS);
        soldierModel.setCurrentDurability(CURRENT_HIT_POINTS);
        soldierModel.setParent(CITIZEN_ID);
        soldierModel.setMetadata(SoldierWeapon.CITIZEN_HIT_POINTS);

        assertThat(result).containsExactlyInAnyOrder(skillModel, citizenModel, energyShieldModel, armorModel, soldierModel);
    }
}