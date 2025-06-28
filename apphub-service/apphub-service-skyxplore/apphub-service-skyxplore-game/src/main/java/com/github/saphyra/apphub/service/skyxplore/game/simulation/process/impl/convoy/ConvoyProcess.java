package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.StringStringMap;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ConvoyProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final UUID convoyId;

    @Getter
    @NonNull
    private final UUID externalReference;
    @NonNull
    private final GameData gameData;
    @NonNull
    private final UUID location;
    @NonNull
    private final ApplicationContextProxy applicationContextProxy;
    @NonNull
    private final Game game;

    @Override
    public ProcessType getType() {
        return ProcessType.CONVOY;
    }

    @Override
    public int getPriority() {
        return gameData.getProcesses()
            .findByIdValidated(externalReference)
            .getPriority() + 1;
    }

    @Override
    public void work() {
        ConvoyProcessHelper helper = applicationContextProxy.getBean(ConvoyProcessHelper.class);
        GameProgressDiff progressDiff = game.getProgressDiff();

        if (status == ProcessStatus.CREATED) {
            log.info("Loading resources...");
            helper.loadResources(progressDiff, gameData, convoyId);

            status = ProcessStatus.IN_PROGRESS;
        }

        if (helper.move(game, location, processId, convoyId)) {
            log.info("Convoy reached its destination.");
            if (helper.unloadResources(progressDiff, gameData, convoyId)) {
                log.info("Convoy unloaded its resources.");
                helper.releaseCitizen(progressDiff, gameData, processId);
                status = ProcessStatus.DONE;
            }
        }
    }

    @Override
    public void cleanup() {
        log.info("Cleaning up {}", this);

        GameProgressDiff progressDiff = game.getProgressDiff();

        ConvoyProcessHelper helper = applicationContextProxy.getBean(ConvoyProcessHelper.class);
        helper.releaseCitizen(progressDiff, gameData, processId);
        helper.cleanup(progressDiff, gameData, convoyId);

        gameData.getProcesses()
            .getByExternalReference(processId)
            .forEach(Process::cleanup);

        status = ProcessStatus.READY_TO_DELETE;

        progressDiff.save(toModel());
    }

    @Override
    public ProcessModel toModel() {
        UuidConverter uuidConverter = applicationContextProxy.getBean(UuidConverter.class);

        ProcessModel model = new ProcessModel();
        model.setId(processId);
        model.setGameId(gameData.getGameId());
        model.setType(GameItemType.PROCESS);
        model.setProcessType(getType());
        model.setStatus(status);
        model.setLocation(location);
        model.setExternalReference(getExternalReference());
        model.setData(new StringStringMap(
            CollectionUtils.toMap(
                new BiWrapper<>(ProcessParamKeys.CONVOY_ID, uuidConverter.convertDomain(convoyId))
            ))
        );
        return model;
    }

    @Override
    public String toString() {
        return String.format(
            "%s(processId=%s, status=%s, externalReference=%s, convoyId=%s)",
            getClass().getSimpleName(),
            processId,
            status,
            externalReference,
            convoyId
        );
    }
}