package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;

import com.github.saphyra.apphub.api.skyxplore.model.game.CitizenAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocationConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.CitizenAllocationFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.cache.SyncCache;
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
class RestProcessHelperTest {
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final Integer MORALE = 23;
    private static final Integer WORK_EFFICIENCY_DROP_UNDER = 24;
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ALLOCATION_ID = UUID.randomUUID();
    private static final Integer REGEN_PER_TICK = 42;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private CitizenAllocationFactory citizenAllocationFactory;

    @Mock
    private CitizenAllocationConverter citizenAllocationConverter;

    @InjectMocks
    private RestProcessHelper underTest;

    @Mock
    private GameData gameData;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenMoraleProperties citizenMoraleProperties;

    @Mock
    private Citizens citizens;

    @Mock
    private Citizen citizen;

    @Mock
    private CitizenAllocations citizenAllocations;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Mock
    private CitizenAllocationModel citizenAllocationModel;

    @Mock
    private SyncCache syncCache;

    @Test
    void getMoraleBasedMultiplier() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(citizenMoraleProperties);
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(citizen.getMorale()).willReturn(MORALE);
        given(citizenMoraleProperties.getWorkEfficiencyDropUnder()).willReturn(WORK_EFFICIENCY_DROP_UNDER);

        double result = underTest.getMoraleBasedMultiplier(gameData, CITIZEN_ID);

        assertThat(result).isEqualTo((double) WORK_EFFICIENCY_DROP_UNDER / MORALE);
    }

    @Test
    void allocateCitizen() {
        given(citizenAllocationFactory.create(CITIZEN_ID, PROCESS_ID)).willReturn(citizenAllocation);
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(gameData.getGameId()).willReturn(GAME_ID);
        given(citizenAllocationConverter.toModel(GAME_ID, citizenAllocation)).willReturn(citizenAllocationModel);

        underTest.allocateCitizen(syncCache, gameData, PROCESS_ID, CITIZEN_ID);

        verify(citizenAllocations).add(citizenAllocation);
        verify(syncCache).saveGameItem(citizenAllocationModel);
    }

    @Test
    void releaseCitizen() {
        given(gameData.getCitizenAllocations()).willReturn(citizenAllocations);
        given(citizenAllocations.findByProcessId(PROCESS_ID)).willReturn(Optional.of(citizenAllocation));
        given(citizenAllocation.getCitizenAllocationId()).willReturn(CITIZEN_ALLOCATION_ID);

        underTest.releaseCitizen(syncCache, gameData, PROCESS_ID);

        verify(citizenAllocations).remove(citizenAllocation);
        verify(syncCache).deleteGameItem(CITIZEN_ALLOCATION_ID, GameItemType.CITIZEN_ALLOCATION);
    }

    @Test
    void increaseMorale() {
        given(gameData.getCitizens()).willReturn(citizens);
        given(citizens.findByCitizenIdValidated(CITIZEN_ID)).willReturn(citizen);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(citizenMoraleProperties);
        given(citizenMoraleProperties.getRegenPerTick()).willReturn(REGEN_PER_TICK);

        underTest.increaseMorale(syncCache, gameData, CITIZEN_ID);

        verify(citizen).increaseMorale(REGEN_PER_TICK);
        verify(syncCache).citizenModified(citizen);
    }
}