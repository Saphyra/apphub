package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.active;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.common.converter.response.CitizenToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priorities;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.Priority;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.priority.PriorityType;
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

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActiveMoraleRechargeProcessTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer MAX_MORALE = 1000;
    private static final Integer CITIZEN_MORALE = 500;
    private static final Double HOUSE_MORALE_MULTIPLIER = 2d;
    private static final Integer REGEN_PER_SECOND = 3;
    private static final Integer MAX_REST_SECONDS = 10;
    private static final Integer MIN_REST_SECONDS = 5;
    private static final Integer RESTORED_MORALE = 25;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private GameData gameData;

    @Mock
    private Planet planet;

    @Mock
    private Citizen citizen;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private CitizenAllocationFactory citizenAllocationFactory;

    private ActiveMoraleRechargeProcess underTest;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenMoraleProperties citizenMoraleProperties;

    @Mock
    private SyncCache syncCache;

    @Mock
    private StorageBuildingService storageBuildingService;

    @Mock
    private StorageBuilding storageBuilding;

    @Mock
    private RestFactory restFactory;

    @Mock
    private Rest rest;

    @Mock
    private ExecutionResult<Rest> executionResult;

    @Mock
    private Future<ExecutionResult<Rest>> future;

    @Mock
    private ExecutorServiceBean executorServiceBean;

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
    private Priorities priorities;

    @Mock
    private Priority priority;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private Game game;

    @BeforeEach
    void setUp() {
        underTest = ActiveMoraleRechargeProcess.builder()
            .processId(PROCESS_ID)
            .status(ProcessStatus.CREATED)
            .gameData(gameData)
            .location(LOCATION)
            .citizen(citizen)
            .applicationContextProxy(applicationContextProxy)
            .build();
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.ACTIVE_MORALE_RECHARGE);
    }

    @Test
    void getExternalReference() {
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        assertThat(underTest.getExternalReference()).isEqualTo(CITIZEN_ID);
    }

    @Test
    void getPriority() {
        given(applicationContextProxy.getBean(GameProperties.class)).willReturn(gameProperties);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(citizenMoraleProperties);
        given(citizenMoraleProperties.getMax()).willReturn(MAX_MORALE);

        given(citizen.getMorale()).willReturn(CITIZEN_MORALE);
        given(gameData.getPriorities()).willReturn(priorities);
        given(priorities.findByLocationAndType(LOCATION, PriorityType.WELL_BEING)).willReturn(priority);
        given(priority.getValue()).willReturn(6);

        int result = underTest.getPriority();

        assertThat(result).isEqualTo(6 * 5 * GameConstants.PROCESS_PRIORITY_MULTIPLIER);
    }

    @Test
    void work_alreadyAssigned() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByCitizenId(CITIZEN_ID)).willReturn(Optional.of(citizenAllocation));
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void work_notTiredEnough() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByCitizenId(CITIZEN_ID)).willReturn(Optional.empty());
        given(applicationContextProxy.getBean(StorageBuildingService.class)).willReturn(storageBuildingService);
        given(storageBuildingService.get(GameConstants.DATA_ID_HOUSE)).willReturn(storageBuilding);
        given(storageBuilding.getData()).willReturn(Map.of(GameConstants.DATA_KEY_MORALE_RECHARGE_MULTIPLIER, HOUSE_MORALE_MULTIPLIER));
        given(applicationContextProxy.getBean(GameProperties.class)).willReturn(gameProperties);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(citizenMoraleProperties);
        given(citizenMoraleProperties.getRegenPerSecond()).willReturn(REGEN_PER_SECOND);
        given(citizenMoraleProperties.getMaxRestSeconds()).willReturn(MAX_REST_SECONDS);
        given(citizen.getMorale()).willReturn(MAX_MORALE - 1);
        given(citizenMoraleProperties.getMinRestSeconds()).willReturn(MIN_REST_SECONDS);

        underTest.work(syncCache);

        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void work() throws ExecutionException, InterruptedException {
        //Start resting
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByCitizenId(CITIZEN_ID)).willReturn(Optional.empty());
        given(applicationContextProxy.getBean(StorageBuildingService.class)).willReturn(storageBuildingService);
        given(storageBuildingService.get(GameConstants.DATA_ID_HOUSE)).willReturn(storageBuilding);
        given(storageBuilding.getData()).willReturn(Map.of(GameConstants.DATA_KEY_MORALE_RECHARGE_MULTIPLIER, HOUSE_MORALE_MULTIPLIER));
        given(applicationContextProxy.getBean(GameProperties.class)).willReturn(gameProperties);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(citizenMoraleProperties);
        given(citizenMoraleProperties.getRegenPerSecond()).willReturn(REGEN_PER_SECOND);
        given(citizenMoraleProperties.getMaxRestSeconds()).willReturn(MAX_REST_SECONDS);
        given(citizen.getMorale()).willReturn(CITIZEN_MORALE);
        given(citizenMoraleProperties.getMinRestSeconds()).willReturn(MIN_REST_SECONDS);
        given(applicationContextProxy.getBean(RestFactory.class)).willReturn(restFactory);
        given(restFactory.create(60, 10000, game)).willReturn(rest);
        given(applicationContextProxy.getBean(ExecutorServiceBean.class)).willReturn(executorServiceBean);
        given(executorServiceBean.asyncProcess(rest)).willReturn(future);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(citizenMoraleProperties.getMax()).willReturn(MAX_MORALE);
        given(citizenAllocationFactory.create(CITIZEN_ID, PROCESS_ID)).willReturn(citizenAllocation);

        given(future.isDone())
            .willReturn(false)
            .willReturn(true);

        underTest.work(syncCache);

        verify(citizenAllocations).add(citizenAllocation);
        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);

        //Finish resting
        given(future.get()).willReturn(executionResult);
        given(executionResult.getOrThrow()).willReturn(rest);
        given(rest.getRestoredMorale()).willReturn(RESTORED_MORALE);
        given(applicationContextProxy.getBean(CitizenToModelConverter.class)).willReturn(citizenToModelConverter);
        given(citizenToModelConverter.convert(GAME_ID, citizen)).willReturn(citizenModel);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(planet.getOwner()).willReturn(USER_ID);
        given(applicationContextProxy.getBean(WsMessageSender.class)).willReturn(messageSender);
        given(applicationContextProxy.getBean(CitizenToResponseConverter.class)).willReturn(citizenToResponseConverter);
        given(citizenToResponseConverter.convert(gameData, citizen)).willReturn(citizenResponse);
        given(planet.getPlanetId()).willReturn(LOCATION);

        underTest.work(syncCache);

        verify(citizen).setMorale(CITIZEN_MORALE + RESTORED_MORALE);
        assertThat(citizenAllocations).isEmpty();
        verify(syncCache).saveGameItem(citizenModel);
        assertThat(underTest.getStatus()).isEqualTo(ProcessStatus.DONE);

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(syncCache).addMessage(eq(USER_ID), eq(WebSocketEventName.SKYXPLORE_GAME_PLANET_CITIZEN_MODIFIED), eq(CITIZEN_ID), argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();
        verify(messageSender).planetCitizenModified(USER_ID, LOCATION, citizenResponse);
    }

    @Test
    void toModel() {
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(planet.getPlanetId()).willReturn(LOCATION);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);

        ProcessModel result = underTest.toModel();

        assertThat(result.getId()).isEqualTo(PROCESS_ID);
        assertThat(result.getGameId()).isEqualTo(GAME_ID);
        assertThat(result.getType()).isEqualTo(GameItemType.PROCESS);
        assertThat(result.getProcessType()).isEqualTo(ProcessType.ACTIVE_MORALE_RECHARGE);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getLocation()).isEqualTo(LOCATION);
        assertThat(result.getExternalReference()).isEqualTo(CITIZEN_ID);
    }
}