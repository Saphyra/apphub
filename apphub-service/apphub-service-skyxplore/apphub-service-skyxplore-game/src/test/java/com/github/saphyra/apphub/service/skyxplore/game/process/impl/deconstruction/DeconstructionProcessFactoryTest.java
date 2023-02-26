package com.github.saphyra.apphub.service.skyxplore.game.process.impl.deconstruction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
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
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private DeconstructionProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private ProcessModel model;

    @Mock
    private Universe universe;

    @Mock
    private Planet planet;

    @Mock
    private Building building;

    @Mock
    private Deconstruction deconstruction;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.DECONSTRUCTION);
    }

    @Test
    void createFromModel() {
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getStatus()).willReturn(ProcessStatus.READY_TO_DELETE);
        given(model.getLocation()).willReturn(PLANET_ID);
        given(model.getExternalReference()).willReturn(DECONSTRUCTION_ID);

        given(game.getUniverse()).willReturn(universe);
        given(universe.findPlanetByIdValidated(PLANET_ID)).willReturn(planet);
        given(planet.findBuildingByDeconstructionIdValidated(DECONSTRUCTION_ID)).willReturn(building);
        given(building.getDeconstruction()).willReturn(deconstruction);

        DeconstructionProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.READY_TO_DELETE);
    }

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);

        DeconstructionProcess result = underTest.create(game, planet, deconstruction);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }
}