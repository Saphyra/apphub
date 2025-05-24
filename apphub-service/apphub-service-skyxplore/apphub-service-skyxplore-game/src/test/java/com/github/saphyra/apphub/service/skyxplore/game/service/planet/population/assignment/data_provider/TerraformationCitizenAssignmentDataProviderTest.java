package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SurfaceType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.surface.Surfaces;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TerraformationCitizenAssignmentDataProviderTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID SURFACE_ID = UUID.randomUUID();

    @InjectMocks
    private TerraformationCitizenAssignmentDataProvider underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Process process;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction terraformation;

    @Mock
    private Surfaces surfaces;

    @Mock
    private Surface surface;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.TERRAFORMATION);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getData() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(process.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(terraformation);
        given(gameData.getSurfaces()).willReturn(surfaces);
        given(terraformation.getExternalReference()).willReturn(SURFACE_ID);
        given(surfaces.findBySurfaceIdValidated(SURFACE_ID)).willReturn(surface);
        given(surface.getSurfaceType()).willReturn(SurfaceType.COAL_FIELD);
        given(terraformation.getData()).willReturn(SurfaceType.DESERT.name());

        Map<String, Object> result = (Map<String, Object>) underTest.getData(gameData, process);

        assertThat(result).containsEntry("originalSurfaceType", SurfaceType.COAL_FIELD);
        assertThat(result).containsEntry("targetSurfaceType", SurfaceType.DESERT.name());
    }
}