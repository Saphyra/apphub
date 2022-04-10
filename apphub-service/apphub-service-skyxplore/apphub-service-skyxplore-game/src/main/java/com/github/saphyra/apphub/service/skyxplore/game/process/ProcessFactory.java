package com.github.saphyra.apphub.service.skyxplore.game.process;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;

public interface ProcessFactory {
    ProcessType getType();

    Process create(Game game, ProcessModel model);
}
