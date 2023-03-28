package com.github.saphyra.apphub.service.skyxplore.game.process.impl.construction;


import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.BuildingDataService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.building.Building;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RequestWorkProcessFactoryForConstructionTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final Integer LEVEL = 3;
    private static final Integer REQUIRED_WORK_POINTS = 10;
    private static final Integer PARALLEL_WORKERS = 2;
    private static final UUID CONSTRUCTION_ID = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private BuildingDataService buildingDataService;

    @Mock
    private RequestWorkProcessFactory requestWorkProcessFactory;

    @InjectMocks
    private RequestWorkProcessFactoryForConstruction underTest;

    @Mock
    private GameData gameData;

    @Mock
    private Building building;

    @Mock
    private BuildingData buildingData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private Construction construction;

    @Mock
    private RequestWorkProcess requestWorkProcess;

    @Test
    public void createRequestWorkProcesses() {
        given(building.getDataId()).willReturn(BUILDING_DATA_ID);
        given(buildingDataService.get(BUILDING_DATA_ID)).willReturn(buildingData);
        given(building.getLevel()).willReturn(LEVEL);
        given(buildingData.getConstructionRequirements()).willReturn(CollectionUtils.singleValueMap(LEVEL + 1, constructionRequirements));
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(constructionRequirements.getParallelWorkers()).willReturn(PARALLEL_WORKERS);

        given(construction.getConstructionId()).willReturn(CONSTRUCTION_ID);

        given(requestWorkProcessFactory.create(gameData, PROCESS_ID, LOCATION, CONSTRUCTION_ID, RequestWorkProcessType.CONSTRUCTION, SkillType.BUILDING, REQUIRED_WORK_POINTS, PARALLEL_WORKERS))
            .willReturn(List.of(requestWorkProcess));

        List<RequestWorkProcess> result = underTest.createRequestWorkProcesses(PROCESS_ID, gameData, LOCATION, building, construction);

        assertThat(result).containsExactly(requestWorkProcess);
    }
}