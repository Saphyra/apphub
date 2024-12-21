package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.deconstruct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeconstructConstructionAreaProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private DeconstructConstructionAreaProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ProcessModel model;

    @Mock
    private Deconstruction deconstruction;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.DECONSTRUCT_CONSTRUCTION_AREA);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getExternalReference()).willReturn(DECONSTRUCTION_ID);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        given(model.getLocation()).willReturn(LOCATION);
        given(game.getData()).willReturn(gameData);

        assertThat(underTest.createFromModel(game, model))
            .returns(PROCESS_ID, DeconstructConstructionAreaProcess::getProcessId)
            .returns(DECONSTRUCTION_ID, DeconstructConstructionAreaProcess::getExternalReference)
            .returns(ProcessStatus.DONE, DeconstructConstructionAreaProcess::getStatus);
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(deconstruction.getDeconstructionId()).willReturn(DECONSTRUCTION_ID);
        given(deconstruction.getLocation()).willReturn(LOCATION);
        given(game.getData()).willReturn(gameData);

        assertThat(underTest.create(game, deconstruction))
            .returns(PROCESS_ID, DeconstructConstructionAreaProcess::getProcessId)
            .returns(DECONSTRUCTION_ID, DeconstructConstructionAreaProcess::getExternalReference)
            .returns(ProcessStatus.CREATED, DeconstructConstructionAreaProcess::getStatus);
    }
}