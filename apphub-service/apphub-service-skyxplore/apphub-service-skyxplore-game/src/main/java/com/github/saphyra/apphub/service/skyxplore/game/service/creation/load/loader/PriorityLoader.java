package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.PriorityModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
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
class PriorityLoader {
    private final GameItemLoader gameItemLoader;

    Map<PriorityType, Integer> load(UUID location) {
        List<PriorityModel> models = gameItemLoader.loadChildren(location, GameItemType.PRIORITY, PriorityModel[].class);
        return models.stream()
            .collect(Collectors.toMap(priorityModel -> PriorityType.valueOf(priorityModel.getPriorityType()), PriorityModel::getValue));
    }
}
