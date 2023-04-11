package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItem;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.Rest;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.RestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

import static java.util.Objects.isNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class PassiveMoraleRechargeProcess implements Process {
    @Getter
    @NonNull
    private final UUID processId;

    @Getter
    @NonNull
    @Builder.Default
    private volatile ProcessStatus status = ProcessStatus.CREATED;

    @NonNull
    private final Game game;

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
        return ProcessType.PASSIVE_MORALE_RECHARGE;
    }

    @Override
    public UUID getExternalReference() {
        return citizen.getCitizenId();
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void work(SyncCache syncCache) {
        if (status == ProcessStatus.CREATED) {
            Optional<CitizenAllocation> maybeAllocation = gameData.getCitizenAllocations().findByCitizenId(citizen.getCitizenId());
            if (maybeAllocation.isPresent()) {
                log.info("Citizen is allocated. {}", maybeAllocation.get());
                status = ProcessStatus.READY_TO_DELETE;
            } else {
                status = ProcessStatus.IN_PROGRESS;
            }
        }

        if (status == ProcessStatus.IN_PROGRESS) {
            if (isNull(restFuture)) {
                startResting();
                CitizenAllocation citizenAllocation = applicationContextProxy.getBean(CitizenAllocationFactory.class)
                    .create(citizen.getCitizenId(), processId);

                gameData.getCitizenAllocations()
                    .add(citizenAllocation);

                CitizenAllocationModel citizenAllocationModel = applicationContextProxy.getBean(CitizenAllocationToModelConverter.class)
                    .convert(gameData.getGameId(), citizenAllocation);
                syncCache.saveGameItem(citizenAllocationModel);
            }

            if (restFuture.isDone()) {
                finishResting(syncCache);
            }
        }
    }

    private void startResting() {
        CitizenMoraleProperties moraleProperties = applicationContextProxy.getBean(GameProperties.class)
            .getCitizen()
            .getMorale();
        int maxPassiveMoraleRecharge = moraleProperties.getRegenPerSecond();

        int moraleToRecharge = Math.min(maxPassiveMoraleRecharge, moraleProperties.getMax() - citizen.getMorale());
        int sleepTimeMilliseconds = (int) Math.round(1000d * moraleToRecharge / maxPassiveMoraleRecharge);

        Rest rest = applicationContextProxy.getBean(RestFactory.class)
            .create(moraleToRecharge, sleepTimeMilliseconds, game);

        restFuture = applicationContextProxy.getBean(ExecutorServiceBean.class)
            .asyncProcess(rest);
    }

    @SneakyThrows
    private void finishResting(SyncCache syncCache) {
        Rest rest = restFuture.get()
            .getOrThrow();
        citizen.setMorale(citizen.getMorale() + rest.getRestoredMorale());

        CitizenAllocation citizenAllocation = gameData.getCitizenAllocations()
            .findByCitizenIdValidated(citizen.getCitizenId());

        gameData.getCitizenAllocations()
            .remove(citizenAllocation);

        syncCache.deleteGameItem(citizenAllocation.getCitizenAllocationId(), GameItemType.CITIZEN_ALLOCATION);

        GameItem citizenModel = applicationContextProxy.getBean(CitizenToModelConverter.class)
            .convert(gameData.getGameId(), citizen);
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
                applicationContextProxy.getBean(CitizenToResponseConverter.class).convert(gameData, citizen)
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
