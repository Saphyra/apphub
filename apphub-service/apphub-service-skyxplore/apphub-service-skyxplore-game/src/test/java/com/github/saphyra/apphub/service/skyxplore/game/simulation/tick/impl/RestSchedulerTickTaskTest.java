package com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest.RestProcess;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest.RestProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.tick.TickTaskOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestSchedulerTickTaskTest {
    private static final UUID ALLOCATED_CITIZEN_ID = UUID.randomUUID();
    private static final UUID IDLE_CITIZEN_ID = UUID.randomUUID();
    private static final UUID RELAXED_CITIZEN_ID = UUID.randomUUID();
    private static final UUID TIRED_CITIZEN_ID = UUID.randomUUID();
    private static final Integer RESTING_MORALE_LIMIT = 9000;
    private static final Integer EXHAUSTED_MORALE_LIMIT = 5000;
    private static final Integer MIN_REST_TICKS = 3;
    private static final Integer EXHAUSTED_REST_TICKS = 34;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private RestProcessFactory restProcessFactory;

    @InjectMocks
    private RestSchedulerTickTask underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private GameProgressDiff progressDiff;

    @Mock
    private Citizen allocatedCitizen;

    @Mock
    private Citizen relaxedCitizen;

    @Mock
    private Citizen tiredCitizen;

    @Mock
    private Citizen idleCitizen;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenMoraleProperties moraleProperties;

    @Mock
    private RestProcess idleProcess;

    @Mock
    private RestProcess tiredProcess;

    @Mock
    private Processes processes;

    @Mock
    private ProcessModel idleModel;

    @Mock
    private ProcessModel tiredModel;

    @Test
    void getOrder() {
        assertThat(underTest.getOrder()).isEqualTo(TickTaskOrder.REST_SCHEDULER);
    }

    @Test
    void process() {
        given(game.getData()).willReturn(gameData);
        given(gameData.getCitizens()).willReturn(CollectionUtils.toList(new Citizens(), allocatedCitizen, relaxedCitizen, tiredCitizen, idleCitizen));
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(allocatedCitizen.getCitizenId()).willReturn(ALLOCATED_CITIZEN_ID);
        given(relaxedCitizen.getCitizenId()).willReturn(RELAXED_CITIZEN_ID);
        given(tiredCitizen.getCitizenId()).willReturn(TIRED_CITIZEN_ID);
        given(idleCitizen.getCitizenId()).willReturn(IDLE_CITIZEN_ID);
        given(citizenAllocations.findByCitizenId(ALLOCATED_CITIZEN_ID)).willReturn(Optional.of(citizenAllocation));
        given(citizenAllocations.findByCitizenId(RELAXED_CITIZEN_ID)).willReturn(Optional.empty());
        given(citizenAllocations.findByCitizenId(TIRED_CITIZEN_ID)).willReturn(Optional.empty());
        given(citizenAllocations.findByCitizenId(IDLE_CITIZEN_ID)).willReturn(Optional.empty());
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getRestingMoraleLimit()).willReturn(RESTING_MORALE_LIMIT);
        given(relaxedCitizen.getMorale()).willReturn(RESTING_MORALE_LIMIT + 1);
        given(idleCitizen.getMorale()).willReturn(EXHAUSTED_MORALE_LIMIT + 1);
        given(tiredCitizen.getMorale()).willReturn(EXHAUSTED_MORALE_LIMIT - 1);
        given(moraleProperties.getExhaustedMorale()).willReturn(EXHAUSTED_MORALE_LIMIT);
        given(moraleProperties.getMinRestTicks()).willReturn(MIN_REST_TICKS);
        given(moraleProperties.getExhaustedRestTicks()).willReturn(EXHAUSTED_REST_TICKS);
        given(restProcessFactory.create(game, idleCitizen, MIN_REST_TICKS)).willReturn(idleProcess);
        given(restProcessFactory.create(game, tiredCitizen, EXHAUSTED_REST_TICKS)).willReturn(tiredProcess);
        given(gameData.getProcesses()).willReturn(processes);
        given(idleProcess.toModel()).willReturn(idleModel);
        given(tiredProcess.toModel()).willReturn(tiredModel);
        given(game.getProgressDiff()).willReturn(progressDiff);

        underTest.process(game);

        verify(processes).add(tiredProcess);
        verify(processes).add(idleProcess);
        verify(progressDiff).save(idleModel);
        verify(progressDiff).save(tiredModel);
    }
}