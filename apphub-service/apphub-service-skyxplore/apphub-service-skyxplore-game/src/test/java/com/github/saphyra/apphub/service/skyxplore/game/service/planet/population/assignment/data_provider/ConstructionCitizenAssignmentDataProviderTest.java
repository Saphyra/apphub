package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Buildings;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructionCitizenAssignmentDataProviderTest {
    private static final String DATA_ID = "data-id";
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_ID = UUID.randomUUID();

    @InjectMocks
    private ConstructionCitizenAssignmentDataProvider underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Process process;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private Buildings buildings;

    @Mock
    private Building building;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONSTRUCTION);
    }

    @Test
    void getData() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(process.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getExternalReference()).willReturn(BUILDING_ID);
        given(gameData.getBuildings()).willReturn(buildings);
        given(construction.getExternalReference()).willReturn(BUILDING_ID);
        given(buildings.findByBuildingId(BUILDING_ID)).willReturn(building);
        given(building.getDataId()).willReturn(DATA_ID);

        assertThat(underTest.getData(gameData, process)).isEqualTo(DATA_ID);
    }
}