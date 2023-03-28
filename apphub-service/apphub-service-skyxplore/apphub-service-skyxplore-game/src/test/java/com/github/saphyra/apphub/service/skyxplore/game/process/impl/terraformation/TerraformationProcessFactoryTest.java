package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TerraformationProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @SuppressWarnings("unused")
    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private TerraformationProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private Surface surface;

    @Mock
    private Construction terraformation;

    @Mock
    private Constructions constructions;

    @Mock
    private Surfaces surfaces;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);


        TerraformationProcess result = underTest.create(gameData, PLANET_ID, surface, terraformation);

        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.TERRAFORMATION);
    }

    @Test
    public void createFromModel() {
        ProcessModel model = new ProcessModel();
        model.setId(PROCESS_ID);
        model.setStatus(ProcessStatus.IN_PROGRESS);
        model.setLocation(PLANET_ID);
        model.setExternalReference(CONSTRUCTION_ID);

        given(game.getData()).willReturn(gameData);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);
        given(terraformation.getExternalReference()).willReturn(SURFACE_ID);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(surfaces.findBySurfaceId(SURFACE_ID)).willReturn(surface);

        TerraformationProcess result = underTest.createFromModel(game, model);

        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }
}