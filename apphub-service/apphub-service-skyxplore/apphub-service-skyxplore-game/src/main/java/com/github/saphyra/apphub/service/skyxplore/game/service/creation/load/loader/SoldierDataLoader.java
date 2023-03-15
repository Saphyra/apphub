package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_data.SoldierWeapon;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_energy_shield.SoldierEnergyShield;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SoldierDataLoader {
    private final DurabilityItemLoader durabilityItemLoader;
    private final ModelToSoldierEnergyShieldConverter energyShieldConverter;
    private final ModelToSoldierArmorConverter soldierArmorConverter;

    SoldierWeapon load(UUID citizenId, String weaponDataId, String meleeWeaponDataId) {
        Map<String, DurabilityItemModel> durabilityItems = durabilityItemLoader.load(citizenId)
            .stream()
            .collect(Collectors.toMap(DurabilityItemModel::getMetadata, Function.identity()));

        DurabilityItemModel soldierItem = durabilityItems.get(SoldierWeapon.CITIZEN_HIT_POINTS);

        return SoldierWeapon.builder()
            .maxHitPoints(soldierItem.getMaxDurability())
            .currentHitPoints(soldierItem.getCurrentDurability())
            .energyShield(Optional.ofNullable(durabilityItems.get(SoldierEnergyShield.CITIZEN_ENERGY_SHIELD)).map(energyShieldConverter::convert).orElse(null))
            .armor(soldierArmorConverter.convert(durabilityItems))
            .weaponDataId(weaponDataId)
            .meleeWeaponDataId(meleeWeaponDataId)
            .build();
    }
}
