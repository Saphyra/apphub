package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.construct_construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.construction_area.ConstructionAreaDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Constructions;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionArea;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction_area.ConstructionAreas;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.processes.Processes;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.storage.StoredResourceService;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConstructConstructionAreaProcessConditionsTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID CONSTRUCTION_AREA_ID = UUID.randomUUID();
    private static final String CONSTRUCTION_AREA_DATA_ID = "construction-area-data-id";
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final Integer AMOUNT_NEEDED = 23;

    @Mock
    private ConstructionAreaDataService constructionAreaDataService;

    @Mock
    private StoredResourceService storedResourceService;

    @InjectMocks
    private ConstructConstructionAreaProcessConditions underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Processes processes;

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

    @Mock
    private ConstructionAreaData constructionAreaData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Test
    void resourcesAvailable() {
        given(gameData.getConstructions()).willReturn(constructions);
        given(constructions.findByIdValidated(CONSTRUCTION_ID)).willReturn(construction);
        given(construction.getExternalReference()).willReturn(CONSTRUCTION_AREA_ID);
        given(gameData.getConstructionAreas()).willReturn(constructionAreas);
        given(constructionAreas.findByIdValidated(CONSTRUCTION_AREA_ID)).willReturn(constructionArea);
        given(constructionArea.getDataId()).willReturn(CONSTRUCTION_AREA_DATA_ID);
        given(constructionAreaDataService.get(CONSTRUCTION_AREA_DATA_ID)).willReturn(constructionAreaData);
        given(constructionAreaData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredResources()).willReturn(Map.of(RESOURCE_DATA_ID, AMOUNT_NEEDED));
        given(storedResourceService.count(gameData, RESOURCE_DATA_ID, PROCESS_ID)).willReturn(AMOUNT_NEEDED);

        assertThat(underTest.resourcesAvailable(gameData, PROCESS_ID, CONSTRUCTION_ID)).isTrue();
    }

    @Test
    void hasWorkProcesses() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.WORK)).willReturn(List.of(process));

        assertThat(underTest.hasWorkProcesses(gameData, PROCESS_ID)).isTrue();
    }

    @Test
    void workFinished() {
        given(gameData.getProcesses()).willReturn(processes);
        given(processes.getByExternalReferenceAndType(PROCESS_ID, ProcessType.WORK)).willReturn(List.of(process));
        given(process.getStatus()).willReturn(ProcessStatus.DONE);

        assertThat(underTest.workFinished(gameData, PROCESS_ID)).isTrue();
    }
}