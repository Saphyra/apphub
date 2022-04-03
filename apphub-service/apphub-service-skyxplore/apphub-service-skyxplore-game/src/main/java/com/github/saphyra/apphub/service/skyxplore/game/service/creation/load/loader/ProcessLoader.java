package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.proxy.GameDataProxy;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
//TODO unit test
class ProcessLoader {
    private final GameItemLoader gameItemLoader;
    private final Map<ProcessType, ProcessFactory> factories;
    private final GameDataProxy gameDataProxy;

    ProcessLoader(GameItemLoader gameItemLoader, List<ProcessFactory> factories, GameDataProxy gameDataProxy) {
        this.gameItemLoader = gameItemLoader;
        this.factories = factories.stream()
            .collect(Collectors.toMap(ProcessFactory::getType, Function.identity()));
        this.gameDataProxy = gameDataProxy;
    }

    public List<Process> load(Game game) {
        List<ProcessModel> models = gameItemLoader.loadChildren(game.getGameId(), GameItemType.PROCESS, ProcessModel[].class);
        models.stream()
            .filter(model -> model.getStatus() == ProcessStatus.READY_TO_DELETE)
            .forEach(model -> gameDataProxy.deleteItem(model.getId(), GameItemType.PROCESS));


        return models.stream()
            .filter(model -> model.getStatus() != ProcessStatus.READY_TO_DELETE)
            .map(model -> factories.get(model.getProcessType()).create(game, model))
            .collect(Collectors.toList());
    }
}
