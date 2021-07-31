package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriorityToModelConverter {
    public List<PriorityModel> convert(Map<PriorityType, Integer> priorities, UUID location, LocationType locationType, Game game) {
        return priorities.entrySet()
            .stream()
            .map(entry -> convert(entry.getKey(), entry.getValue(), location, locationType, game.getGameId()))
            .collect(Collectors.toList());
    }

    public PriorityModel convert(PriorityType priorityType, Integer value, UUID location, LocationType locationType, UUID gameId) {
        PriorityModel model = new PriorityModel();
        model.setGameId(gameId);
        model.setType(GameItemType.PRIORITY);
        model.setLocation(location);
        model.setLocationType(locationType.name());
        model.setValue(value);
        model.setPriorityType(priorityType.name());
        return model;
    }
}
