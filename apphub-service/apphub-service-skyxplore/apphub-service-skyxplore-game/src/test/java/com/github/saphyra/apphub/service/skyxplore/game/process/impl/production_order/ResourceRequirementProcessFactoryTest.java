package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.cache.SyncCache;
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
public class ResourceRequirementProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String DATA_ID = "data-id";
    private static final int AMOUNT = 31;
    private static final String PRODUCER_BUILDING_DATA_ID = "producer-building-data-id";
    private static final String REQUIRED_RESOURCE_DATA_ID = "required-resource-data-id";
    private static final Integer REQUIRED_RESOURCE_AMOUNT = 34;
    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID RESERVED_STORAGE_ID = UUID.randomUUID();

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private ProductionRequirementsAllocationService productionRequirementsAllocationService;

    @Mock
    private ProductionOrderProcessFactory productionOrderProcessFactory;

    @InjectMocks
    private ResourceRequirementProcessFactory underTest;

    @Mock
    private SyncCache syncCache;

    @Mock
    private Game game;

    @Mock
    private Planet planet;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private ProductionData productionData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private ProductionOrderProcess productionOrderProcess;

    @Test
    public void createResourceRequirementProcesses() {
        given(productionBuildingService.get(PRODUCER_BUILDING_DATA_ID)).willReturn(productionBuilding);
        given(productionBuilding.getGives()).willReturn(CollectionUtils.singleValueMap(DATA_ID, productionData));
        given(productionData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredResources()).willReturn(CollectionUtils.singleValueMap(REQUIRED_RESOURCE_DATA_ID, REQUIRED_RESOURCE_AMOUNT));
        given(game.getGameId()).willReturn(GAME_ID);
        given(productionRequirementsAllocationService.allocate(syncCache, GAME_ID, planet, PROCESS_ID, REQUIRED_RESOURCE_DATA_ID, AMOUNT * REQUIRED_RESOURCE_AMOUNT)).willReturn(RESERVED_STORAGE_ID);
        given(productionOrderProcessFactory.create(PROCESS_ID, game, planet, RESERVED_STORAGE_ID)).willReturn(List.of(productionOrderProcess));

        List<ProductionOrderProcess> result = underTest.createResourceRequirementProcesses(syncCache, PROCESS_ID, game, planet, DATA_ID, AMOUNT, PRODUCER_BUILDING_DATA_ID);

        assertThat(result).containsExactly(productionOrderProcess);
    }
}