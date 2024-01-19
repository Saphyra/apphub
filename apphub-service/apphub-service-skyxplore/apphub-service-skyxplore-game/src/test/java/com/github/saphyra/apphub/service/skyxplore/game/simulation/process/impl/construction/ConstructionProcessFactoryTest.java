package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructionProcessFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ConstructionProcessFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Game game;

    @Mock
    private ProcessModel model;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);

        ConstructionProcess result = underTest.create(game, LOCATION, CONSTRUCTION_ID);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
        assertThat(result.getExternalReference()).isEqualTo(CONSTRUCTION_ID);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        given(game.getData()).willReturn(gameData);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getExternalReference()).willReturn(CONSTRUCTION_ID);

        ConstructionProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.DONE);
        assertThat(result.getExternalReference()).isEqualTo(CONSTRUCTION_ID);
    }
}