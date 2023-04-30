package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WorkProcessFactoryTest {
    private static final UUID PROCESS_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_REFERENCE = UUID.randomUUID();
    private static final UUID LOCATION = UUID.randomUUID();
    private static final String BUILDING_DATA_ID = "building-data-id";
    private static final int REQUIRED_WORK_POINTS = 70;
    private static final UUID TARGET_ID = UUID.randomUUID();
    private static final int COMPLETED_WORK_POINTS = 324;
    private static final String RESOURCE_DATA_ID = "resource-data-id";
    private static final Integer AMOUNT = 10;
    private static final Integer WORK_POINTS_PER_RESOURCE = 5;
    private static final Integer MAX_WORK_POINT_BATCH = 100;

    @Mock
    private ProductionBuildingService productionBuildingService;

    @Mock
    private GameProperties gameProperties;

    @Spy
    private final UuidConverter uuidConverter = new UuidConverter();

    @Mock
    private ApplicationContextProxy applicationContextProxy;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private WorkProcessFactory underTest;

    @Mock
    private Game game;

    @Mock
    private GameData gameData;

    @Mock
    private ProcessModel model;

    @Mock
    private ProductionBuildingData productionBuildingData;

    @Mock
    private ProductionData productionData;

    @Mock
    private ConstructionRequirements constructionRequirements;

    @Mock
    private CitizenProperties citizenProperties;

    @Mock
    private DeconstructionProperties deconstructionProperties;

    @Test
    void getType() {
        assertThat(underTest.getType()).isEqualTo(ProcessType.WORK);
    }

    @Test
    void createFromModel() {
        given(game.getData()).willReturn(gameData);
        given(model.getId()).willReturn(PROCESS_ID);
        given(model.getExternalReference()).willReturn(EXTERNAL_REFERENCE);
        given(model.getLocation()).willReturn(LOCATION);
        given(model.getStatus()).willReturn(ProcessStatus.DONE);
        Map<String, String> data = Map.of(
            ProcessParamKeys.BUILDING_DATA_ID, BUILDING_DATA_ID,
            ProcessParamKeys.SKILL_TYPE, SkillType.AIMING.name(),
            ProcessParamKeys.REQUIRED_WORK_POINTS, String.valueOf(REQUIRED_WORK_POINTS),
            ProcessParamKeys.WORK_PROCESS_TYPE, WorkProcessType.CONSTRUCTION.name(),
            ProcessParamKeys.TARGET_ID, uuidConverter.convertDomain(TARGET_ID),
            ProcessParamKeys.COMPLETED_WORK_POINTS, String.valueOf(COMPLETED_WORK_POINTS)
        );
        given(model.getData()).willReturn(data);

        WorkProcess result = underTest.createFromModel(game, model);

        assertThat(result.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.getStatus()).isEqualTo(ProcessStatus.DONE);
    }

    @Test
    void createForProduction() {
        given(productionBuildingService.get(BUILDING_DATA_ID)).willReturn(productionBuildingData);
        given(productionBuildingData.getGives()).willReturn(Map.of(RESOURCE_DATA_ID, productionData));
        given(productionData.getConstructionRequirements()).willReturn(constructionRequirements);
        given(constructionRequirements.getRequiredWorkPoints()).willReturn(WORK_POINTS_PER_RESOURCE);
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMaxWorkPointsBatch()).willReturn(MAX_WORK_POINT_BATCH);
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(productionData.getRequiredSkill()).willReturn(SkillType.AIMING);

        List<WorkProcess> result = underTest.createForProduction(gameData, EXTERNAL_REFERENCE, LOCATION, BUILDING_DATA_ID, RESOURCE_DATA_ID, AMOUNT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.get(0).getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.get(0).getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void createForDeconstruction() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMaxWorkPointsBatch()).willReturn(MAX_WORK_POINT_BATCH);
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);
        given(gameProperties.getDeconstruction()).willReturn(deconstructionProperties);
        given(deconstructionProperties.getRequiredWorkPoints()).willReturn(REQUIRED_WORK_POINTS);

        List<WorkProcess> result = underTest.createForDeconstruction(gameData, EXTERNAL_REFERENCE, TARGET_ID, LOCATION);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.get(0).getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.get(0).getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void createForTerraformation() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMaxWorkPointsBatch()).willReturn(MAX_WORK_POINT_BATCH);
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);

        List<WorkProcess> result = underTest.createForTerraformation(gameData, EXTERNAL_REFERENCE, TARGET_ID, LOCATION, REQUIRED_WORK_POINTS);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.get(0).getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.get(0).getStatus()).isEqualTo(ProcessStatus.CREATED);
    }

    @Test
    void createForConstruction() {
        given(gameProperties.getCitizen()).willReturn(citizenProperties);
        given(citizenProperties.getMaxWorkPointsBatch()).willReturn(MAX_WORK_POINT_BATCH);
        given(idGenerator.randomUuid()).willReturn(PROCESS_ID);

        List<WorkProcess> result = underTest.createForConstruction(gameData, EXTERNAL_REFERENCE, TARGET_ID, LOCATION, REQUIRED_WORK_POINTS);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(result.get(0).getExternalReference()).isEqualTo(EXTERNAL_REFERENCE);
        assertThat(result.get(0).getStatus()).isEqualTo(ProcessStatus.CREATED);
    }
}