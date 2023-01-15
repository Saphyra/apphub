package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.BodyPart;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierArmorPiece;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierEnergyShield;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SoldierDataLoaderTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final String WEAPON_DATA_ID = "weapon-data-id";
    private static final Integer MAX_HIT_POINTS = 43;
    private static final Integer CURRENT_HIT_POINTS = 645;
    private static final String MELEE_WEAPON_DATA_ID = "melee-weapon-data-id";

    @Mock
    private DurabilityItemLoader durabilityItemLoader;

    @Mock
    private ModelToSoldierEnergyShieldConverter energyShieldConverter;

    @Mock
    private ModelToSoldierArmorConverter soldierArmorConverter;

    @InjectMocks
    private SoldierDataLoader underTest;

    @Mock
    private DurabilityItemModel hitPointsModel;

    @Mock
    private DurabilityItemModel energyShieldModel;

    @Mock
    private SoldierEnergyShield energyShield;

    @Mock
    private SoldierArmorPiece armorPiece;

    @Test
    public void load_energyShield() {
        given(hitPointsModel.getMetadata()).willReturn(SoldierData.CITIZEN_HIT_POINTS);
        given(energyShieldModel.getMetadata()).willReturn(SoldierEnergyShield.CITIZEN_ENERGY_SHIELD);
        given(durabilityItemLoader.load(CITIZEN_ID)).willReturn(Arrays.asList(hitPointsModel, energyShieldModel));

        given(hitPointsModel.getMaxDurability()).willReturn(MAX_HIT_POINTS);
        given(hitPointsModel.getCurrentDurability()).willReturn(CURRENT_HIT_POINTS);

        given(energyShieldConverter.convert(energyShieldModel)).willReturn(energyShield);

        ConcurrentHashMap<BodyPart, SoldierArmorPiece> armor = new ConcurrentHashMap<>(CollectionUtils.singleValueMap(BodyPart.HEAD, armorPiece));
        given(soldierArmorConverter.convert(CollectionUtils.toMap(
            new BiWrapper<>(SoldierData.CITIZEN_HIT_POINTS, hitPointsModel),
            new BiWrapper<>(SoldierEnergyShield.CITIZEN_ENERGY_SHIELD, energyShieldModel)
        ))).willReturn(armor);

        SoldierData result = underTest.load(CITIZEN_ID, WEAPON_DATA_ID, MELEE_WEAPON_DATA_ID);

        assertThat(result.getWeaponDataId()).isEqualTo(WEAPON_DATA_ID);
        assertThat(result.getMeleeWeaponDataId()).isEqualTo(MELEE_WEAPON_DATA_ID);
        assertThat(result.getMaxHitPoints()).isEqualTo(MAX_HIT_POINTS);
        assertThat(result.getCurrentHitPoints()).isEqualTo(CURRENT_HIT_POINTS);
        assertThat(result.getEnergyShield()).isEqualTo(energyShield);
        assertThat(result.getArmor()).isEqualTo(armor);
    }

    @Test
    public void load_noEnergyShield() {
        given(hitPointsModel.getMetadata()).willReturn(SoldierData.CITIZEN_HIT_POINTS);
        given(durabilityItemLoader.load(CITIZEN_ID)).willReturn(Arrays.asList(hitPointsModel));

        given(hitPointsModel.getMaxDurability()).willReturn(MAX_HIT_POINTS);
        given(hitPointsModel.getCurrentDurability()).willReturn(CURRENT_HIT_POINTS);

        ConcurrentHashMap<BodyPart, SoldierArmorPiece> armor = new ConcurrentHashMap<>(CollectionUtils.singleValueMap(BodyPart.HEAD, armorPiece));
        given(soldierArmorConverter.convert(CollectionUtils.singleValueMap(SoldierData.CITIZEN_HIT_POINTS, hitPointsModel))).willReturn(armor);

        SoldierData result = underTest.load(CITIZEN_ID, WEAPON_DATA_ID, MELEE_WEAPON_DATA_ID);

        assertThat(result.getWeaponDataId()).isEqualTo(WEAPON_DATA_ID);
        assertThat(result.getMeleeWeaponDataId()).isEqualTo(MELEE_WEAPON_DATA_ID);
        assertThat(result.getMaxHitPoints()).isEqualTo(MAX_HIT_POINTS);
        assertThat(result.getCurrentHitPoints()).isEqualTo(CURRENT_HIT_POINTS);
        assertThat(result.getEnergyShield()).isNull();
        assertThat(result.getArmor()).isEqualTo(armor);
    }
}