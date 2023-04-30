package com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability;

import com.github.saphyra.apphub.api.skyxplore.model.game.DurabilityModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DurabilityToModelConverter {
    public List<DurabilityModel> convert(UUID gameId, Collection<Durability> durabilities) {
        return durabilities.stream()
            .map(durability -> convert(gameId, durability))
            .collect(Collectors.toList());
    }

    public DurabilityModel convert(UUID gameId, Durability durability) {
        DurabilityModel model = new DurabilityModel();

        model.setId(durability.getDurabilityId());
        model.setGameId(gameId);
        model.setType(GameItemType.DURABILITY);

        model.setExternalReference(durability.getExternalReference());
        model.setMaxHitPoints(durability.getMaxHitPoints());
        model.setCurrentHitPoints(durability.getCurrentHitPoints());

        return model;
    }
}
