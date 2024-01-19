package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
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
class DeconstructionProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private DeconstructionProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ProcessModel model;


    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.DECONSTRUCTION);
    }

    @Test
    void createFromModel() {
        given(game.getData()).willReturn(gameData);
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        given(model.getExternalReference()).willReturn(DECONSTRUCTION_ID);
        given(model.getLocation()).willReturn(LOCATION);

        DeconstructionProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(game.getData()).willReturn(gameData);

        DeconstructionProcess result = underTest.create(game, LOCATION, DECONSTRUCTION_ID);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(DECONSTRUCTION_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }
}