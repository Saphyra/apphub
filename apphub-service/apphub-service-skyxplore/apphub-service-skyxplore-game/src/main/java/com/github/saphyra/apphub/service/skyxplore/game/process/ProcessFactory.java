package com.github.saphyra.apphub.service.skyxplore.game.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;

public interface ProcessFactory {
    ProcessType getType();

    Process createFromModel(Game game, ProcessModel model);
}
