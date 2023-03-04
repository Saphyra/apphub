package com.github.saphyra.apphub.service.skyxplore.game.process.impl.morale.passive;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PassiveMoraleRechargeProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private PassiveMoraleRechargeProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private Citizen citizen;

    @Mock
    private ProcessModel processModel;

    @Mock
    private Universe universe;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.PASSIVE_MORALE_RECHARGE);
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);

        PassiveMoraleRechargeProcess result = underTest.create(game, planet, citizen);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void createFromModel() {
        given(processModel.getId()).willReturn(PROCESS_ID);
        given(processModel.getLocation()).willReturn(PLANET_ID);
        given(processModel.getExternalReference()).willReturn(CITIZEN_ID);
        given(processModel.getStatus()).willReturn(ProcessStatus.IN_PROGRESS);

        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getPopulation()).willReturn(Map.of(CITIZEN_ID, citizen));

        PassiveMoraleRechargeProcess result = underTest.createFromModel(game, processModel);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }
}