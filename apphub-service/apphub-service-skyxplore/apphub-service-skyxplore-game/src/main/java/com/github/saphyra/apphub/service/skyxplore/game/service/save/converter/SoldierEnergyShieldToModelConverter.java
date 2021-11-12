package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierEnergyShield;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierEnergyShield.CITIZEN_ENERGY_SHIELD;


@Component
@RequiredArgsConstructor
@Slf4j
class SoldierEnergyShieldToModelConverter {
    DurabilityItemModel convert(UUID citizenId, UUID gameId, SoldierEnergyShield energyShield) {
        DurabilityItemModel model = new DurabilityItemModel();
        model.setId(energyShield.getEntityId());
        model.setType(GameItemType.DURABILITY_ITEM_MODEL);
        model.setGameId(gameId);
        model.setMaxDurability(energyShield.getMaxDurability());
        model.setCurrentDurability(energyShield.getCurrentDurability());
        model.setParent(citizenId);
        model.setMetadata(CITIZEN_ENERGY_SHIELD);
        model.setDataId(energyShield.getDataId());

        return model;
    }
}
