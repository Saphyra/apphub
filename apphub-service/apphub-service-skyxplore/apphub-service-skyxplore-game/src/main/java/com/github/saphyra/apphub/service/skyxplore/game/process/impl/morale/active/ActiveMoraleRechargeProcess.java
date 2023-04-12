package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.active;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.Rest;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.RestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class ActiveMoraleRechargeProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    @Builder.Default
    private volatile ProcessStatus status = ProcessStatus.CREATED;

    @NonNull
    private final GameData gameData;

    @NonNull
    private final UUID location;

    @NonNull
    private final Citizen citizen;

    @NonNull
    private final ApplicationContextProxy applicationContextProxy;

    private Future<ExecutionResult<Rest>> restFuture;

    @Override
    public ProcessType getType() {
        return ProcessType.ACTIVE_MORALE_RECHARGE;
    }

    @Override
    public UUID getExternalReference() {
        return citizen.getCitizenId();
    }

    @Override
    public int getPriority() {
        int maxMorale = applicationContextProxy.getBean(GameProperties.class)
            .getCitizen()
            .getMorale()
            .getMax();

        double citizenMoraleRatio = Math.ceil((double) (maxMorale - citizen.getMorale()) / maxMorale * 10);

        log.info("Citizen {} has {} morale, so priority multiplier is {}", citizen.getCitizenId(), citizen.getMorale(), citizenMoraleRatio);

        int result = (int) (gameData.getPriorities().findByLocationAndType(location, PriorityType.WELL_BEING).getValue() * citizenMoraleRatio * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
        log.info("Priority {} calculated for Citizen {} active resting.", result, citizen.getCitizenId());
        return result;
    }

    @Override
    public void work(SyncCache syncCache) {
        if (status == ProcessStatus.CREATED) {
            if (gameData.getCitizenAllocations().findByCitizenId(citizen.getCitizenId()).isPresent()) {
                status = ProcessStatus.READY_TO_DELETE;
            } else {
                status = ProcessStatus.IN_PROGRESS;
            }
        }

        if (status == ProcessStatus.IN_PROGRESS) {
            if (isNull(restFuture)) {
                log.info("Sending citizen {} to rest...", citizen);

                startResting(syncCache);
            }
        }

        if (status == ProcessStatus.IN_PROGRESS) {
            if (restFuture.isDone()) {
                finishResting(syncCache);
            } else {
                log.info("Citizen {} is still resting.", citizen.getCitizenId());
            }
        }
    }

    private void startResting(SyncCache syncCache) {
        StorageBuilding storageBuilding = applicationContextProxy.getBean(StorageBuildingService.class)
            .get(GameConstants.DATA_ID_HOUSE);
        double houseMoraleMultiplier = Double.parseDouble(storageBuilding.getData().get(GameConstants.DATA_KEY_MORALE_RECHARGE_MULTIPLIER).toString());

        CitizenMoraleProperties moraleProperties = applicationContextProxy.getBean(GameProperties.class)
            .getCitizen()
            .getMorale();
        double moralePerSecond = houseMoraleMultiplier * moraleProperties.getRegenPerSecond();
        double maxMoraleRecharge = moraleProperties.getMaxRestSeconds() * moralePerSecond;

        int moraleToRecharge = (int) Math.min(maxMoraleRecharge, moraleProperties.getMax() - citizen.getMorale());
        int sleepTimeMilliseconds = (int) Math.round(1000d * moraleToRecharge / moralePerSecond);

        if (sleepTimeMilliseconds < moraleProperties.getMinRestSeconds() * 1000) {
            log.debug("Rest time {}ms is smaller than MinRestSeconds {}s", sleepTimeMilliseconds, moraleProperties.getMinRestSeconds());
            status = ProcessStatus.READY_TO_DELETE;
            return;
        }

        log.info("House recharges {} morale per second.", moralePerSecond);
        log.info("Maximum {} morale can be restored at once, {} for {} seconds.", maxMoraleRecharge, moralePerSecond, moraleProperties.getMaxRestSeconds());
        log.info("Citizen {} can recharge {} morale now. It will take {} milliseconds.", citizen.getCitizenId(), moraleToRecharge, sleepTimeMilliseconds);

        Game game = applicationContextProxy.getBean(GameDao.class)
            .findById(gameData.getGameId());

        Rest rest = applicationContextProxy.getBean(RestFactory.class)
            .create(moraleToRecharge, sleepTimeMilliseconds, game);

        restFuture = applicationContextProxy.getBean(ExecutorServiceBean.class)
            .asyncProcess(rest);

        CitizenAllocation citizenAllocation = applicationContextProxy.getBean(CitizenAllocationFactory.class)
            .create(citizen.getCitizenId(), processId);
        gameData.getCitizenAllocations()
            .add(citizenAllocation);

        CitizenAllocationModel citizenAllocationModel = applicationContextProxy.getBean(CitizenAllocationToModelConverter.class)
            .convert(gameData.getGameId(), citizenAllocation);
        syncCache.saveGameItem(citizenAllocationModel);
    }

    @SneakyThrows
    private void finishResting(SyncCache syncCache) {
        Rest rest = restFuture.get()
            .getOrThrow();
        citizen.setMorale(citizen.getMorale() + rest.getRestoredMorale());

        log.info("Citizen {} morale increased by {}, current morale: {}", citizen.getCitizenId(), rest.getRestoredMorale(), citizen.getMorale());

        CitizenAllocation citizenAllocation = gameData.getCitizenAllocations()
            .findByCitizenIdValidated(citizen.getCitizenId());

        gameData.getCitizenAllocations()
                .deleteByProcessId(processId);
        syncCache.deleteGameItem(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);

        GameItem citizenModel = applicationContextProxy.getBean(CitizenConverter.class)
            .toModel(gameData.getGameId(), citizen);
        syncCache.saveGameItem(citizenModel);

        UUID ownerId = gameData.getPlanets()
            .get(location)
            .getOwner();

        syncCache.addMessage(
            ownerId,
            WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
            citizen.getCitizenId(),
            () -> applicationContextProxy.getBean(WsMessageSender.class).planetCitizenModified(
                ownerId,
                location,
                applicationContextProxy.getBean(CitizenConverter.class).toResponse(gameData, citizen)
            )
        );

        status = ProcessStatus.DONE;
    }

    @Override
    public void cancel(SyncCache syncCache) {
        throw new UnsupportedOperationException("PassiveMoraleRecharge process cannot be cancelled. (Or citizen will be very angry)");
    }

    @Override
    public ProcessModel toModel() {
        ProcessModel model = new ProcessModel();
        model.setId(processId);
        model.setGameId(gameData.getGameId());
        model.setType(GameItemType.PROCESS);
        model.setProcessType(getType());
        model.setStatus(getStatus());
        model.setLocation(location);
        model.setExternalReference(getExternalReference());
        return model;
    }
}
