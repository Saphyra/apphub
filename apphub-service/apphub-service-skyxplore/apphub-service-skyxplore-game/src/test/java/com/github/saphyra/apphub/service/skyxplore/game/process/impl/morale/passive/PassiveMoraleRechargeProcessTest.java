package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
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
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.Rest;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.rest.RestFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.save.converter.CitizenToModelConverter;
import com.github.saphyra.apphub.service.skyxplore.game.ws.WsMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PassiveMoraleRechargeProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer REGEN_PER_SECOND = 50;
    private static final Integer MAX_MORALE = 1000;
    private static final Integer RESTORED_MORALE = 42;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();

    private PassiveMoraleRechargeProcess underTest;

    @Mock
    private Game game;

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

    @BeforeEach
    void setUp() {
        underTest = PassiveMoraleRechargeProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .game(game)
            .planet(planet)
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
        given(planet.getCitizenAllocations()).willReturn(CollectionUtils.singleValueMap(CITIZEN_ID, UUID.randomUUID(), new CitizenAllocations()));

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void work() throws ExecutionException, InterruptedException {
        //Initialize rest
        CitizenAllocations citizenAllocations = new CitizenAllocations();
        given(planet.getCitizenAllocations()).willReturn(citizenAllocations);

        given(applicationContextProxy.getBean(GameProperties.class)).willReturn(gameProperties);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getRegenPerSecond()).willReturn(REGEN_PER_SECOND);
        given(moraleProperties.getMax()).willReturn(MAX_MORALE);
        given(citizen.getMorale()).willReturn(MAX_MORALE - 20);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        given(applicationContextProxy.getBean(RestFactory.class))
            .willReturn(restFactory);
        given(restFactory.create(20, (int) Math.round(1000d * 20 / REGEN_PER_SECOND), game)).willReturn(rest);

        given(applicationContextProxy.getBean(ExecutorServiceBean.class)).willReturn(executorServiceBean);
        given(executorServiceBean.asyncProcess(rest)).willReturn(future);
        given(future.isDone())
            .willReturn(false)
            .willReturn(true);

        underTest.work(syncCache);

        assertThat(citizenAllocations).containsEntry(CITIZEN_ID, PROCESS_ID);
        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        verifyNoInteractions(syncCache);

        //Finish rest
        given(future.get()).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(rest);
        given(rest.getRestoredMorale()).willReturn(RESTORED_MORALE);
        given(applicationContextProxy.getBean(CitizenToModelConverter.class)).willReturn(citizenToModelConverter);
        given(game.getGameId()).willReturn(GAME_ID);
        given(citizenToModelConverter.convert(citizen, GAME_ID)).willReturn(citizenModel);

        given(planet.getOwner()).willReturn(USER_ID);
        given(applicationContextProxy.getBean(WsMessageSender.class)).willReturn(messageSender);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(applicationContextProxy.getBean(CitizenToResponseConverter.class)).willReturn(citizenToResponseConverter);
        given(citizenToResponseConverter.convert(citizen)).willReturn(citizenResponse);

        underTest.work(syncCache);

        verify(citizen).setMorale(MAX_MORALE - 20 + RESTORED_MORALE);
        verify(syncCache).saveGameItem(citizenModel);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED), eq(CITIZEN_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetCitizenModified(USER_ID, PLANET_ID, citizenResponse);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    @Test
    void toModel() {
        given(game.getGameId()).willReturn(GAME_ID);
        given(planet.getPlanetId()).willReturn(PLANET_ID);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.PASSIVE_MORALE_RECHARGE);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(PLANET_ID);
        assertThat(result.getLocationType()).isEqualTo(LocationType.PLANET.name());
        assertThat(result.getExternalReference()).isEqualTo(CITIZEN_ID);
    }
}