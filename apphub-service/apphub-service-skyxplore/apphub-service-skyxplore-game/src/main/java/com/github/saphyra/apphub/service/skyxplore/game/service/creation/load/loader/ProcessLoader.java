package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessLoader extends AbstractGameItemLoader<ProcessModel> {
    private final List<ProcessFactory> processFactories;

    public ProcessLoader(GameItemLoader gameItemLoader, List<ProcessFactory> processFactories) {
        super(gameItemLoader);
        this.processFactories = processFactories;
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.PROCESS;
    }

    @Override
    protected Class<ProcessModel[]> getArrayClass() {
        return ProcessModel[].class;
    }

    public void loadProcesses(Game game) {
        getByGameId(game.getGameId())
            .stream()
            .map(processModel -> selectProcessFactory(processModel.getProcessType()).createFromModel(game, processModel))
            .forEach(process -> game.getData().getProcesses().add(process));
    }

    private ProcessFactory selectProcessFactory(ProcessType processType) {
        return processFactories.stream()
            .filter(processFactory -> processFactory.getType() == processType)
            .findFirst()
            .orElseThrow(() -> ExceptionFactory.reportedException("ProcessFactory not found for ProcessType " + processType));
    }
}
