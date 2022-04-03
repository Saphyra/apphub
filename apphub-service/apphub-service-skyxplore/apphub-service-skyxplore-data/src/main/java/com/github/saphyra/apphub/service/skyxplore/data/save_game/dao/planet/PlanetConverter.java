package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.planet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PlanetModel;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.UuidMap;
import com.github.saphyra.apphub.lib.common_util.collection.UuidStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetConverter extends ConverterBase<PlanetEntity, PlanetModel> {
    private final UuidConverter uuidConverter;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    protected PlanetModel processEntityConversion(PlanetEntity entity) {
        PlanetModel model = new PlanetModel();
        model.setId(uuidConverter.convertEntity(entity.getPlanetId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.PLANET);
        model.setSolarSystemId(uuidConverter.convertEntity(entity.getSolarSystemId()));
        model.setDefaultName(entity.getDefaultName());
        model.setCustomNames(objectMapperWrapper.readValue(entity.getCustomNames(), UuidStringMap.class));
        model.setSize(entity.getSize());
        model.setOwner(uuidConverter.convertEntity(entity.getOwner()));
        TypeReference<Map<UUID, List<UUID>>> typeReference = new TypeReference<>() {
        };
        model.setBuildingAllocations(objectMapperWrapper.readValue(entity.getBuildingAllocations(), typeReference)); //TODO unit test
        model.setCitizenAllocations(objectMapperWrapper.readValue(entity.getCitizenAllocations(), UuidMap.class)); //TODO unit test
        return model;
    }

    @Override
    protected PlanetEntity processDomainConversion(PlanetModel domain) {
        PlanetEntity result = PlanetEntity.builder()
            .planetId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .solarSystemId(uuidConverter.convertDomain(domain.getSolarSystemId()))
            .defaultName(domain.getDefaultName())
            .customNames(objectMapperWrapper.writeValueAsString(domain.getCustomNames()))
            .size(domain.getSize())
            .owner(uuidConverter.convertDomain(domain.getOwner()))
            .buildingAllocations(objectMapperWrapper.writeValueAsString(domain.getBuildingAllocations())) //TODO unit test
            .citizenAllocations(objectMapperWrapper.writeValueAsString(domain.getCitizenAllocations())) //TODO unit test
            .build();
        log.debug("Converted planet: {}", result);
        return result;
    }
}
