package com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
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
public class PriorityConverter {
    public List<PriorityModel> toModel(UUID gameId, Collection<Priority> priorities) {
        return priorities.stream()
            .map(priority -> toModel(gameId, priority))
            .collect(Collectors.toList());
    }


    public PriorityModel toModel(UUID gameId, Priority priority) {
        PriorityModel model = new PriorityModel();
        model.setId(priority.getPriorityId());
        model.setGameId(gameId);
        model.setType(GameItemType.PRIORITY);
        model.setLocation(priority.getLocation());
        model.setValue(priority.getValue());
        model.setPriorityType(priority.getType().name());
        return model;
    }
}
