package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityItemModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.BodyPart;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.SoldierArmorPiece;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ModelToSoldierArmorConverter {
    ConcurrentMap<BodyPart, SoldierArmorPiece> convert(Map<String, DurabilityItemModel> items) {
        return Arrays.stream(BodyPart.values())
            .filter(bodyPart -> items.containsKey(bodyPart.getMetadata()))
            .collect(Collectors.toConcurrentMap(Function.identity(), bodyPart -> convert(items.get(bodyPart.getMetadata()))));
    }

    private SoldierArmorPiece convert(DurabilityItemModel model) {
        return SoldierArmorPiece.builder()
            .entityId(model.getId())
            .dataId(model.getDataId())
            .maxDurability(model.getMaxDurability())
            .currentDurability(model.getCurrentDurability())
            .build();
    }
}
