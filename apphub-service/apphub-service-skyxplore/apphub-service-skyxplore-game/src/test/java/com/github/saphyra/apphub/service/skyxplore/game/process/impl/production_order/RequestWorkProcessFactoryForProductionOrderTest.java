package com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuilding;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage.ReservedStorage;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.RequestWorkProcessFactory;
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
public class RequestWorkProcessFactoryForProductionOrderTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final String PRODUCER_BUILDING_DATA_ID = "producer-building-data-id";
    private static final String DATA_ID = "data-id";
    private static final Integer RESERVED_AMOUNT = 3;
    private static final Integer REQUIRED_WORK_POINTS = 2;
    private static final Integer MAX_WORK_POINTS_BATCH = 5;
    private static final UUID LOCATION = UUID.randomUUID();

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private GameProperties gameProperties;

    @Mock
    private RequestWorkProcessFactory requestWorkProcessFactory;

    @InjectMocks
    private RequestWorkProcessFactoryForProductionOrder underTest;

    @Mock
    private GameData gameData;

    @Mock
    private ReservedStorage reservedStorage;

    @Mock
    private ProductionBuilding productionBuilding;

    @Mock
    private ProductionData productionData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private RequestWorkProcess requestWorkProcess1;

    @Mock
    private RequestWorkProcess requestWorkProcess2;

    @Test
    public void createWorkPointProcesses() {
        given(productionBuildingService.get(PRODUCER_BUILDING_DATA_ID)).willReturn(productionBuilding);
        given(reservedStorage.getDataId()).willReturn(DATA_ID);
        given(productionBuilding.getGives()).willReturn(CollectionUtils.toMap(DATA_ID, productionData));
        given(reservedStorage.getAmount()).willReturn(RESERVED_AMOUNT);
        given(productionData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMaxWorkPointsBatch()).willReturn(MAX_WORK_POINTS_BATCH);
        given(productionData.getRequiredSkill()).willReturn(SkillType.AIMING);
        given(requestWorkProcessFactory.create(PROCESS_ID, gameData, LOCATION, PRODUCER_BUILDING_DATA_ID, SkillType.AIMING, 5)).willReturn(requestWorkProcess1);
        given(requestWorkProcessFactory.create(PROCESS_ID, gameData, LOCATION, PRODUCER_BUILDING_DATA_ID, SkillType.AIMING, 1)).willReturn(requestWorkProcess2);

        List<RequestWorkProcess> result = underTest.createWorkPointProcesses(PROCESS_ID, gameData, LOCATION, PRODUCER_BUILDING_DATA_ID, reservedStorage);

        assertThat(result).containsExactlyInAnyOrder(requestWorkProcess1, requestWorkProcess2);
    }
}