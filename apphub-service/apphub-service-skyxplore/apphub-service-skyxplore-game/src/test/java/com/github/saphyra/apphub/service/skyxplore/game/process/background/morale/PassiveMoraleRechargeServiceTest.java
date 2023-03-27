package com.github.saphyra.apphub.service.skyxplore.game.process.background.morale;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizens;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen_allocation.CitizenAllocation;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive.PassiveMoraleRechargeProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive.PassiveMoraleRechargeProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PassiveMoraleRechargeServiceTest {
    private static final Integer MAX_MORALE = 2345;
    private static final UUID ASSIGNED_CITIZEN_ID = UUID.randomUUID();
    private static final UUID TIRED_CITIZEN_ID = UUID.randomUUID();

    @Mock
    private GameProperties gameProperties;

    @Mock
    private PassiveMoraleRechargeProcessFactory passiveMoraleRechargeProcessFactory;

    @InjectMocks
    private PassiveMoraleRechargeService underTest;

    @Mock
    private GameData gameData;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Citizen tiredCitizen;

    @Mock
    private Citizen relaxedCitizen;

    @Mock
    private Citizen assignedCitizen;

    @Mock
    private PassiveMoraleRechargeProcess process;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Processes processes;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private CitizenMoraleProperties moraleProperties;

    @Mock
    private CitizenAllocation citizenAllocation;

    @Test
    void processGame() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getMax()).willReturn(MAX_MORALE);

        given(tiredCitizen.getMorale()).willReturn(MAX_MORALE - 1);
        given(relaxedCitizen.getMorale()).willReturn(MAX_MORALE);
        given(assignedCitizen.getMorale()).willReturn(MAX_MORALE - 1);

        given(tiredCitizen.getCitizenId()).willReturn(TIRED_CITIZEN_ID);
        given(assignedCitizen.getCitizenId()).willReturn(ASSIGNED_CITIZEN_ID);

        given(gameData.getCitizens()).willReturn(CollectionUtils.toList(new Citizens(), tiredCitizen, relaxedCitizen, assignedCitizen));
        given(gameData.getCitizenAllocations().findByCitizenId(ASSIGNED_CITIZEN_ID)).willReturn(Optional.of(citizenAllocation));
        given(gameData.getProcesses()).willReturn(processes);

        given(passiveMoraleRechargeProcessFactory.create(gameData, tiredCitizen)).willReturn(process);
        given(process.toModel()).willReturn(processModel);

        underTest.processGame(gameData, syncCache);

        verify(syncCache).saveGameItem(processModel);
        verify(processes).add(process);
    }
}