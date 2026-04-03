package com.github.saphyra.apphub.service.feature.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.feature.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.feature.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.feature.skyxplore.game.simulation.process.Process;

public interface CitizenAssignmentDataProvider {
    ProcessType getType();

    Object getData(GameData gameData, Process process);
}
