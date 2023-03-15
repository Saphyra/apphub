package com.github.saphyra.apphub.service.skyxplore.game.process.impl.terraformation;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameConstants;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
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
    private Planet planet;

    @Mock
    private Surface surface;

    @Mock
    private Construction terraformation;

    @Mock
    private Universe universe;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(surface.getTerraformation()).willReturn(terraformation);

        TerraformationProcess result = underTest.create(game, planet, surface);

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

        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.getSurfaces()).willReturn(CollectionUtils.singleValueMap(GameConstants.ORIGO, surface, new SurfaceMap()));
        given(surface.getTerraformation()).willReturn(terraformation);
        given(terraformation.getConstructionId()).willReturn(CONSTRUCTION_ID);

        TerraformationProcess result = underTest.createFromModel(game, model);

        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }
}