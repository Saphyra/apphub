package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
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
class TerraformationProcessFactoryTest {
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID TERRAFORMATION_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private TerraformationProcessFactory underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Construction terraformation;

    @Mock
    private ProcessModel model;

    @Mock
    private Game game;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(terraformation.getConstructionId()).willReturn(TERRAFORMATION_ID);
        given(game.getData()).willReturn(gameData);

        TerraformationProcess result = underTest.create(game, LOCATION, terraformation);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(TERRAFORMATION_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.TERRAFORMATION);
    }

    @Test
    void createFromModel() {
        given(game.getData()).willReturn(gameData);
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getExternalReference()).willReturn(TERRAFORMATION_ID);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);

        TerraformationProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(TERRAFORMATION_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.DONE);
    }
}