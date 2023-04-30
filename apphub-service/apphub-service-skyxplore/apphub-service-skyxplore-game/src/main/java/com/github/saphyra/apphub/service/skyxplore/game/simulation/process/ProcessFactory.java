package com.github.saphyra.apphub.service.skyxplore.game.simulation.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;

public interface ProcessFactory {
    ProcessType getType();

    Process createFromModel(Game game, ProcessModel model);
}
