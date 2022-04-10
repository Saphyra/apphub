package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ConstructionProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID PLANET_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @SuppressWarnings("unused")
    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @InjectMocks
    private ConstructionProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private Universe universe;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(building.getConstruction()).willReturn(construction);

        ConstructionProcess result = underTest.create(game, planet, building);

        assertThat(result.getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    public void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONSTRUCTION);
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
        given(planet.findBuildingByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(building);
        given(building.getConstruction()).willReturn(construction);

        ConstructionProcess result = underTest.create(game, model);

        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }
}