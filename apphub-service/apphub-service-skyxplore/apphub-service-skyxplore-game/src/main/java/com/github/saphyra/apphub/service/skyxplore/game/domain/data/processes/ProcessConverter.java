package com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessConverter implements GameDataToModelConverter {
    @Override
    public List<ProcessModel> convert(UUID gameId, GameData gameData) {
        return gameData.getProcesses()
            .stream()
            .map(Process::toModel)
            .collect(Collectors.toList());
    }
}
