package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalMap;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenLoader {
    private final GameItemLoader gameItemLoader;
    private final SkillLoader skillLoader;
    private final SoldierDataLoader soldierDataLoader;

    public OptionalMap<UUID, Citizen> load(UUID location) {
        List<CitizenModel> models = gameItemLoader.loadChildren(location, GameItemType.CITIZEN, CitizenModel[].class);
        Map<UUID, Citizen> citizens = models.stream()
            .map(this::convert)
            .collect(Collectors.toMap(Citizen::getCitizenId, Function.identity()));
        return new OptionalHashMap<>(citizens);
    }

    private Citizen convert(CitizenModel model) {
        return Citizen.builder()
            .citizenId(model.getId())
            .location(model.getLocation())
            .locationType(LocationType.valueOf(model.getLocationType()))
            .name(model.getName())
            .morale(model.getMorale())
            .satiety(model.getSatiety())
            .skills(skillLoader.load(model.getId()))
            .soldierData(soldierDataLoader.load(model.getId(), model.getWeaponDataId(), model.getMeleeWeaponDataId()))
            .build();
    }
}
