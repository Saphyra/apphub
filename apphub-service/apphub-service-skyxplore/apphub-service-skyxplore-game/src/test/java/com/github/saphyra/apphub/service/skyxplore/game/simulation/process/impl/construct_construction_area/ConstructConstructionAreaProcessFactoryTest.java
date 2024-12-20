package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructConstructionAreaProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ConstructConstructionAreaProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ProcessModel model;

    @Mock
    private Construction construction;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONSTRUCT_CONSTRUCTION_AREA);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        given(model.getLocation()).willReturn(LOCATION);
        given(game.getData()).willReturn(gameData);

        assertThat(underTest.createFromModel(game, model))
            .returns(PROCESS_ID, ConstructConstructionAreaProcess::getProcessId)
            .returns(CONSTRUCTION_ID, ConstructConstructionAreaProcess::getExternalReference)
            .returns(ProcessStatus.DONE, ConstructConstructionAreaProcess::getStatus);
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);
        given(construction.getLocation()).willReturn(LOCATION);
        given(game.getData()).willReturn(gameData);

        assertThat(underTest.create(game, construction))
            .returns(PROCESS_ID, ConstructConstructionAreaProcess::getProcessId)
            .returns(CONSTRUCTION_ID, ConstructConstructionAreaProcess::getExternalReference)
            .returns(ProcessStatus.CREATED, ConstructConstructionAreaProcess::getStatus);
    }
}