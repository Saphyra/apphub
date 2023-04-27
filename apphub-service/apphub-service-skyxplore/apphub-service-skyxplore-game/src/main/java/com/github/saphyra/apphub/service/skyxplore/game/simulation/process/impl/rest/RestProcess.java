package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class RestProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    private volatile ProcessStatus status;

    @NonNull
    private final GameData gameData;

    @NonNull
    private final UUID citizenId;

    @NonNull
    private UUID location;

    @NonNull
    private final Integer restForTicks;

    @NonNull
    private volatile Integer restedForTicks;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    @Override
    public ProcessType getType() {
        return ProcessType.REST;
    }

    @Override
    public UUID getExternalReference() {
        return citizenId;
    }

    @Override
    public int getPriority() {
        double moraleBasedMultiplier = applicationContextProxy.getBean(RestProcessHelper.class)
            .getMoraleBasedMultiplier(gameData, citizenId);

        return (int) (gameData.getPriorities().findByLocationAndType(location, PriorityType.WELL_BEING).getValue() * 5 * GameConstants.PROCESS_PRIORITY_MULTIPLIER * moraleBasedMultiplier);
    }

    @Override
    public void work(SyncCache syncCache) {
        log.info("Working on {}", this);

        RestProcessConditions conditions = applicationContextProxy.getBean(RestProcessConditions.class);

        if (status == ProcessStatus.CREATED) {
            if (conditions.citizenAllocated(gameData, citizenId)) {
                log.info("Citizen is allocated.");
                status = ProcessStatus.READY_TO_DELETE;
                return;
            }

            status = ProcessStatus.IN_PROGRESS;
        }

        RestProcessHelper helper = applicationContextProxy.getBean(RestProcessHelper.class);

        if (!conditions.citizenAllocated(gameData, citizenId)) {
            helper.allocateCitizen(syncCache, gameData, processId, citizenId);
        }

        if (restedForTicks >= restForTicks) {
            status = ProcessStatus.READY_TO_DELETE;
            return;
        }

        restedForTicks += 1;
        helper.increaseMorale(syncCache, gameData, citizenId);
    }

    @Override
    public void cleanup(SyncCache syncCache) {
        RestProcessHelper helper = applicationContextProxy.getBean(RestProcessHelper.class);

        helper.releaseCitizen(syncCache, gameData, processId);
    }

    @Override
    public ProcessModel toModel() {
        ProcessModel model = new ProcessModel();
        model.setId(processId);
        model.setGameId(gameData.getGameId());
        model.setType(GameItemType.PROCESS);
        model.setProcessType(getType());
        model.setStatus(status);
        model.setLocation(location);
        model.setExternalReference(getExternalReference());
        model.setData(CollectionUtils.toMap(
            new BiWrapper<>(ProcessParamKeys.REST_FOR_TICKS, String.valueOf(restForTicks)),
            new BiWrapper<>(ProcessParamKeys.RESTED_FOR_TICKS, String.valueOf(restedForTicks))
        ));

        return model;
    }

    @Override
    public String toString() {
        return String.format("%s(processId=%s, status=%s)", getClass().getSimpleName(), processId, status);
    }
}
