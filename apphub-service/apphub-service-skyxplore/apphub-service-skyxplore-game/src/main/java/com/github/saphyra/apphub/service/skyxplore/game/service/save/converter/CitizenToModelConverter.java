package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_energy_shield.SoldierEnergyShield;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.service.skyxplore.game.domain.data.soldier_data.SoldierWeapon.CITIZEN_HIT_POINTS;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenToModelConverter {
    private final SkillToModelConverter skillConverter;
    private final SoldierEnergyShieldToModelConverter energyShieldConverter;
    private final SoldierArmorPieceToModelConverter soldierArmorPieceConverter;

    public List<GameItem> convertDeep(Collection<Citizen> citizens, Game game) {
        return citizens.stream()
            .map(citizen -> convertDeep(citizen, game.getGameId()))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public List<GameItem> convertDeep(Citizen citizen, UUID gameId) {
        List<GameItem> result = new ArrayList<>();
        result.add(convert(citizen, gameId));
        result.addAll(skillConverter.convert(citizen.getSkills().values(), gameId));
        SoldierEnergyShield energyShield = citizen.getSoldierData().getEnergyShield();
        if (nonNull(energyShield)) {
            result.add(energyShieldConverter.convert(citizen.getCitizenId(), gameId, energyShield));
        }
        result.addAll(soldierArmorPieceConverter.convert(citizen.getCitizenId(), gameId, citizen.getSoldierData().getArmor()));
        result.add(convertSoldierData(citizen, gameId));
        return result;
    }

    public GameItem convert(Citizen citizen, UUID gameId) {
        CitizenModel model = new CitizenModel();
        model.setId(citizen.getCitizenId());
        model.setGameId(gameId);
        model.setType(GameItemType.CITIZEN);
        model.setLocation(citizen.getLocation());
        model.setLocationType(citizen.getLocationType().name());
        model.setName(citizen.getName());
        model.setMorale(citizen.getMorale());
        model.setSatiety(citizen.getSatiety());
        model.setWeaponDataId(citizen.getSoldierData().getRangedWeaponDataId());

        return model;
    }

    private DurabilityItemModel convertSoldierData(Citizen citizen, UUID gameId) {
        DurabilityItemModel model = new DurabilityItemModel();
        model.setId(citizen.getCitizenId());
        model.setGameId(gameId);
        model.setType(GameItemType.DURABILITY_ITEM_MODEL);
        model.setMaxDurability(citizen.getSoldierData().getMaxHitPoints());
        model.setCurrentDurability(citizen.getSoldierData().getCurrentHitPoints());
        model.setParent(citizen.getCitizenId());
        model.setMetadata(CITIZEN_HIT_POINTS);
        return model;
    }
}
