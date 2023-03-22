package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
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
public class PriorityToModelConverter {
    public List<PriorityModel> convert(UUID gameId, Collection<Priority> priorities) {
        return priorities.stream()
            .map(priority -> convert(gameId, priority))
            .collect(Collectors.toList());
    }


    public PriorityModel convert(UUID gameId, Priority priority) {
        PriorityModel model = new PriorityModel();
        model.setGameId(gameId);
        model.setType(GameItemType.PRIORITY);
        model.setLocation(priority.getLocation());
        model.setValue(priority.getValue());
        model.setPriorityType(priority.getType().name());
        return model;
    }
}
