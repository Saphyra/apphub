package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TerraformationProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;

    public TerraformationProcess create(GameData gameData, UUID location, Surface surface, Construction terraformation) {
        return TerraformationProcess.builder()
            .processId(idGenerator.randomUuid())
            .status(ProcessStatus.CREATED)
            .gameData(gameData)
            .location(location)
            .surface(surface)
            .terraformation(terraformation)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Override
    public ProcessType getType() {
        return ProcessType.TERRAFORMATION;
    }

    @Override
    public TerraformationProcess createFromModel(Game game, ProcessModel model) {
        Construction terraformation = game.getData()
            .getConstructions()
            .findByIdValidated(model.getExternalReference());

        Surface surface = game.getData()
            .getSurfaces()
            .findBySurfaceId(terraformation.getExternalReference());

        return TerraformationProcess.builder()
            .processId(model.getId())
            .status(model.getStatus())
            .gameData(game.getData())
            .location(model.getLocation())
            .surface(surface)
            .terraformation(terraformation)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }
}
