package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConstructionProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

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
    private GameData gameData;

    @Mock
    private Building building;

    @Mock
    private Construction construction;

    @Mock
    private Buildings buildings;

    @Mock
    private Constructions constructions;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);

        ConstructionProcess result = underTest.create(gameData, LOCATION, building, construction);

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
        model.setLocation(LOCATION);
        model.setExternalReference(CONSTRUCTION_ID);

        given(game.getData()).willReturn(gameData);
        given(gameData.getBuildings()).willReturn(buildings);
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getExternalReference()).willReturn(BUILDING_ID);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);

        ConstructionProcess result = underTest.createFromModel(game, model);

        assertThat(result.getStatus()).isEqualTo(ProcessStatus.IN_PROGRESS);
    }
}