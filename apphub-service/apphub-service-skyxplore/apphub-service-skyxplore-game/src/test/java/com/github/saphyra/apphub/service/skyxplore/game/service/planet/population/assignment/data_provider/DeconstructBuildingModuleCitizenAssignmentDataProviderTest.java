package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModule;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building_module.BuildingModules;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstruction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.deconstruction.Deconstructions;
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
class DeconstructBuildingModuleCitizenAssignmentDataProviderTest {
    private static final String DATA_ID = "data-id";
    private static final UUID DECONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID BUILDING_MODULE_ID = UUID.randomUUID();

    @InjectMocks
    private DeconstructBuildingModuleCitizenAssignmentDataProvider underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Process process;

    @Mock
    private Deconstructions deconstructions;

    @Mock
    private Deconstruction deconstruction;

    @Mock
    private BuildingModules buildingModules;

    @Mock
    private BuildingModule buildingModule;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.DECONSTRUCT_BUILDING_MODULE);
    }

    @Test
    void getData() {
        given(gameData.getDeconstructions()).willReturn(deconstructions);
        given(process.getExternalReference()).willReturn(DECONSTRUCTION_ID);
        given(deconstructions.findByDeconstructionIdValidated(DECONSTRUCTION_ID)).willReturn(deconstruction);
        given(deconstruction.getExternalReference()).willReturn(BUILDING_MODULE_ID);
        given(gameData.getBuildingModules()).willReturn(buildingModules);
        given(buildingModules.findByBuildingModuleIdValidated(BUILDING_MODULE_ID)).willReturn(buildingModule);
        given(buildingModule.getDataId()).willReturn(DATA_ID);

        assertThat(underTest.getData(gameData, process)).isEqualTo(DATA_ID);
    }
}