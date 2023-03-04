package com.github.saphyra.apphub.service.skyxplore.game.process.background.morale;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.CitizenAllocations;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive.PassiveMoraleRechargeProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive.PassiveMoraleRechargeProcessFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
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
    private Game game;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Universe universe;

    @Mock
    private SolarSystem solarSystem;

    @Mock
    private Planet planet;

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

    @Test
    void processGame() {
        given(game.getUniverse()).willReturn(universe);
        given(universe.getSystems()).willReturn(Map.of(GameConstants.ORIGO, solarSystem));
        given(solarSystem.getPlanets()).willReturn(Map.of(UUID.randomUUID(), planet));
        given(planet.getPopulation()).willReturn(Map.of(UUID.randomUUID(), tiredCitizen, UUID.randomUUID(), relaxedCitizen, UUID.randomUUID(), assignedCitizen));
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMorale()).willReturn(moraleProperties);
        given(moraleProperties.getMax()).willReturn(MAX_MORALE);

        given(tiredCitizen.getMorale()).willReturn(MAX_MORALE - 1);
        given(relaxedCitizen.getMorale()).willReturn(MAX_MORALE);
        given(assignedCitizen.getMorale()).willReturn(MAX_MORALE - 1);

        given(tiredCitizen.getCitizenId()).willReturn(TIRED_CITIZEN_ID);
        given(assignedCitizen.getCitizenId()).willReturn(ASSIGNED_CITIZEN_ID);

        given(planet.getCitizenAllocations()).willReturn(CollectionUtils.singleValueMap(ASSIGNED_CITIZEN_ID, UUID.randomUUID(), new CitizenAllocations()));
        given(passiveMoraleRechargeProcessFactory.create(game, planet, tiredCitizen)).willReturn(process);
        given(process.toModel()).willReturn(processModel);
        given(game.getProcesses()).willReturn(processes);

        underTest.processGame(game, syncCache);

        verify(syncCache).saveGameItem(processModel);
        verify(processes).add(process);
    }
}