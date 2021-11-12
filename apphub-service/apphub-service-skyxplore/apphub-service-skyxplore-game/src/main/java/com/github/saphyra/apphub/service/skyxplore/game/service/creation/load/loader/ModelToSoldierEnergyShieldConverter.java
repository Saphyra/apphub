package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierEnergyShield;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ModelToSoldierEnergyShieldConverter {
    SoldierEnergyShield convert(DurabilityItemModel model) {
        return SoldierEnergyShield.builder()
            .entityId(model.getId())
            .dataId(model.getDataId())
            .maxDurability(model.getMaxDurability())
            .currentDurability(model.getCurrentDurability())
            .build();
    }
}
