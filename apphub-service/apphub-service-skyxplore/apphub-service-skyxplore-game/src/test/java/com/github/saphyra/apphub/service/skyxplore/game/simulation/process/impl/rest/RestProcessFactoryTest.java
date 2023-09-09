package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.rest;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
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
class RestProcessFactoryTest {
    private static final int REST_FOR_TICKS = 32;
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CITIZEN_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final int RESTED_FOR_TICKS = 2;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private RestProcessFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Citizen citizen;

    @Mock
    private Game game;

    @Mock
    private ProcessModel model;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.REST);
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(citizen.getCitizenId()).willReturn(CITIZEN_ID);
        given(citizen.getLocation()).willReturn(LOCATION);

        RestProcess result = underTest.create(gameData, citizen, REST_FOR_TICKS);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(CITIZEN_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void createFromModel() {
        given(game.getData()).willReturn(gameData);
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        given(model.getExternalReference()).willReturn(CITIZEN_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getData()).willReturn(Map.of(
            ProcessParamKeys.REST_FOR_TICKS, String.valueOf(REST_FOR_TICKS),
            ProcessParamKeys.RESTED_FOR_TICKS, String.valueOf(RESTED_FOR_TICKS)
        ));

        RestProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(CITIZEN_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.DONE);
    }
}