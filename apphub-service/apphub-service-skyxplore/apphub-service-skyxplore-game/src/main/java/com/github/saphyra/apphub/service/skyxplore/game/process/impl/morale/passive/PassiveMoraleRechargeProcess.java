package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
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
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.Process;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.Rest;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.RestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
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
    private final Planet planet;

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
            if (planet.getCitizenAllocations().containsKey(citizen.getCitizenId())) {
                status = ProcessStatus.READY_TO_DELETE;
            } else {
                status = ProcessStatus.IN_PROGRESS;
            }
        }

        if (status == ProcessStatus.IN_PROGRESS) {
            if (isNull(restFuture)) {
                startResting();
                planet.getCitizenAllocations()
                    .put(citizen.getCitizenId(), processId);
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
        planet.getCitizenAllocations().remove(citizen.getCitizenId());

        GameItem citizenModel = applicationContextProxy.getBean(CitizenToModelConverter.class)
            .convert(citizen, game.getGameId());
        syncCache.saveGameItem(citizenModel);

        syncCache.addMessage(
            planet.getOwner(),
            WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED,
            citizen.getCitizenId(),
            () -> applicationContextProxy.getBean(WsMessageSender.class).planetCitizenModified(
                planet.getOwner(),
                planet.getPlanetId(),
                applicationContextProxy.getBean(CitizenToResponseConverter.class).convert(citizen)
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
        model.setGameId(game.getGameId());
        model.setType(GameItemType.PROCESS);
        model.setProcessType(getType());
        model.setStatus(getStatus());
        model.setLocation(planet.getPlanetId());
        model.setLocationType(LocationType.PLANET.name());
        model.setExternalReference(getExternalReference());
        return model;
    }
}
