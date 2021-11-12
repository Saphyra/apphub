package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.citizen;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenConverter extends ConverterBase<CitizenEntity, CitizenModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected CitizenModel processEntityConversion(CitizenEntity entity) {
        CitizenModel model = new CitizenModel();
        model.setId(uuidConverter.convertEntity(entity.getCitizenId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.CITIZEN);
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setLocationType(entity.getLocationType());
        model.setName(entity.getName());
        model.setMorale(entity.getMorale());
        model.setSatiety(entity.getSatiety());
        model.setWeaponDataId(entity.getWeaponDataId());
        model.setMeleeWeaponDataId(entity.getMeleeWeaponDataId());
        return model;
    }

    @Override
    protected CitizenEntity processDomainConversion(CitizenModel domain) {
        return CitizenEntity.builder()
            .citizenId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .locationType(domain.getLocationType())
            .name(domain.getName())
            .morale(domain.getMorale())
            .satiety(domain.getSatiety())
            .weaponDataId(domain.getWeaponDataId())
            .meleeWeaponDataId(domain.getMeleeWeaponDataId())
            .build();
    }
}
