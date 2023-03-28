package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planets;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.Rest;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.RestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PassiveMoraleRechargeProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer REGEN_PER_SECOND = 50;
    private static final Integer MAX_MORALE = 1000;
    private static final Integer RESTORED_MORALE = 42;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();

    private PassiveMoraleRechargeProcess underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Citizen citizen;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private SyncCache syncCache;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenMoraleProperties moraleProperties;

    @Mock
    private RestFactory restFactory;

    @Mock
    private Rest rest;

    @Mock
    private Future<ExecutionResult<Rest>> future;

    @Mock
    private ExecutionResult<Rest> executionResult;

    @Mock
    private CitizenToModelConverter citizenToModelConverter;

    @Mock
    private CitizenModel citizenModel;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private CitizenToResponseConverter citizenToResponseConverter;

    @Mock
    private CitizenResponse citizenResponse;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private CitizenAllocationFactory citizenAllocationFactory;

    @Mock
    private CitizenAllocationToModelConverter citizenAllocationToModelConverter;

    @Mock
    private CitizenAllocationModel citizenAllocationModel;

    @BeforeEach
    void setUp() {
        underTest = PassiveMoraleRechargeProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .game(game)
            .gameData(gameData)
            .location(LOCATION)
            .citizen(citizen)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PASSIVE_MORALE_RECHARGE);
    }

    @Test
    void getExternalReference() {
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        assertThat(underTest.getExternalReference()).isEqualTo(CITIZEN_ID);
    }

    @Test
    void getPriority() {
        assertThat(underTest.getPriority()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void work_citizenAllocated() {
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByCitizenId(CITIZEN_ID)).willReturn(Optional.of(citizenAllocation));

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void work() throws ExecutionException, InterruptedException {
        //Initialize rest
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(citizenAllocations.findByCitizenId(CITIZEN_ID)).willReturn(Optional.empty());

        given(applicationContextProxy.getBean(GameProperties.class)).willReturn(gameProperties);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getRegenPerSecond()).willReturn(REGEN_PER_SECOND);
        given(moraleProperties.getMax()).willReturn(MAX_MORALE);
        given(citizen.getMorale()).willReturn(MAX_MORALE - 20);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(citizenAllocationFactory.create(CITIZEN_ID, PROCESS_ID)).willReturn(citizenAllocation);
        given(applicationContextProxy.getBean(CitizenAllocationFactory.class)).willReturn(citizenAllocationFactory);
        given(applicationContextProxy.getBean(CitizenAllocationToModelConverter.class)).willReturn(citizenAllocationToModelConverter);
        given(citizenAllocationToModelConverter.convert(GAME_ID, citizenAllocation)).willReturn(citizenAllocationModel);

        given(applicationContextProxy.getBean(RestFactory.class))
            .willReturn(restFactory);
        given(restFactory.create(20, (int) Math.round(1000d * 20 / REGEN_PER_SECOND), game)).willReturn(rest);

        given(applicationContextProxy.getBean(ExecutorServiceBean.class)).willReturn(executorServiceBean);
        given(executorServiceBean.asyncProcess(rest)).willReturn(future);
        given(future.isDone())
            .willReturn(false)
            .willReturn(true);

        underTest.work(syncCache);

        verify(citizenAllocations).add(citizenAllocation);
        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verify(syncCache).saveGameItem(citizenAllocationModel);

        //Finish rest
        given(future.get()).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(rest);
        given(rest.getRestoredMorale()).willReturn(RESTORED_MORALE);
        given(applicationContextProxy.getBean(CitizenToModelConverter.class)).willReturn(citizenToModelConverter);
        given(citizenToModelConverter.convert(GAME_ID, citizen)).willReturn(citizenModel);

        given(planet.getOwner()).willReturn(USER_ID);
        given(applicationContextProxy.getBean(WsMessageSender.class)).willReturn(messageSender);
        given(applicationContextProxy.getBean(CitizenToResponseConverter.class)).willReturn(citizenToResponseConverter);
        given(citizenToResponseConverter.convert(gameData, citizen)).willReturn(citizenResponse);
        given(citizenAllocation.getCitizenAllocationId()).willReturn(CITIZEN_ALLOCATION_ID);
        given(citizenAllocations.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizenAllocation);
        given(gameData.getPlanets()).willReturn(CollectionUtils.toMap(LOCATION, planet, new Planets()));

        underTest.work(syncCache);

        verify(syncCache).deleteGameItem(CITIZEN_ALLOCATION_ID, GameItemType.CITIZEN_ALLOCATION);
        verify(citizen).setMorale(MAX_MORALE - 20 + RESTORED_MORALE);
        verify(syncCache).saveGameItem(citizenModel);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED), eq(CITIZEN_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetCitizenModified(USER_ID, LOCATION, citizenResponse);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.PASSIVE_MORALE_RECHARGE);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(CITIZEN_ID);
    }
}