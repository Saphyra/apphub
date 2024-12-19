package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.assignment.data_provider;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
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
class ConstructConstructionAreaCitizenAssignmentDataProviderTest {
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";

    @InjectMocks
    private ConstructConstructionAreaCitizenAssignmentDataProvider underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Process process;

    @Mock
    private Constructions constructions;

    @Mock
    private Construction construction;

    @Mock
    private ConstructionAreas constructionAreas;

    @Mock
    private ConstructionArea constructionArea;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.CONSTRUCT_CONSTRUCTION_AREA);
    }

    @Test
    void getData() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(process.getExternalReference()).willReturn(CONSTRUCTION_ID);
        given(constructions.findByConstructionIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(construction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);
        given(constructionAreas.findByConstructionAreaIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getDataId()).willReturn(DATA_ID);

        assertThat(underTest.getData(gameData, process)).isEqualTo(DATA_ID);
    }
}