package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.ConstructionRequirements;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionData;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.Process;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class WorkProcessFactory implements ProcessFactory {
    private final ProductionBuildingService productionBuildingService;
    private final GameProperties gameProperties;
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

    @Override
    public ProcessType getType() {
        return ProcessType.WORK;
    }

    @Override
    public Process createFromModel(Game game, ProcessModel model) {
        return create(
            model.getId(),
            model.getExternalReference(),
            game.getData(),
            model.getLocation(),
            model.getStatus(),
            model.getData().get(ProcessParamKeys.BUILDING_DATA_ID),
            SkillType.valueOf(model.getData().get(ProcessParamKeys.SKILL_TYPE)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.REQUIRED_WORK_POINTS)),
            WorkProcessType.valueOf(model.getData().get(ProcessParamKeys.REQUEST_WORK_PROCESS_TYPE)),
            uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.TARGET_ID)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.COMPLETED_WORK_POINTS))
        );
    }

    public List<WorkProcess> createForProduction(GameData gameData, UUID processId, UUID location, String producerBuildingDataId, String dataId, Integer amount) {
        log.info("Creating WorkPointProcesses...");

        ProductionData productionData = productionBuildingService.get(producerBuildingDataId)
            .getGives()
            .get(dataId);
        ConstructionRequirements constructionRequirements = productionData.getConstructionRequirements();
        int requiredWorkPoints = amount * constructionRequirements.getRequiredWorkPoints();

        int maxWorkPointsBatch = gameProperties.getCitizen()
            .getMaxWorkPointsBatch();

        log.info("RequiredWorkPoints: {}, MaxWorkPointsBatch: {}", requiredWorkPoints, maxWorkPointsBatch);

        List<WorkProcess> result = new ArrayList<>();

        for (int workPointsLeft = requiredWorkPoints; workPointsLeft > 0; workPointsLeft -= maxWorkPointsBatch) {
            WorkProcess workProcess = createNew(
                gameData,
                processId,
                location,
                producerBuildingDataId,
                productionData.getRequiredSkill(),
                Math.min(workPointsLeft, maxWorkPointsBatch),
                WorkProcessType.OTHER,
                null
            );
            log.info("{} created.", workProcess);
            result.add(workProcess);
        }

        return result;
    }

    public List<WorkProcess> createForDeconstruction(GameData gameData, UUID processId, UUID deconstructionId, UUID location) {
        DeconstructionProperties deconstructionProperties = gameProperties.getDeconstruction();

        int maxWorkPointsBatch = gameProperties.getCitizen()
            .getMaxWorkPointsBatch();

        List<WorkProcess> result = new ArrayList<>();

        for (int workPointsLeft = deconstructionProperties.getRequiredWorkPoints(); workPointsLeft > 0; workPointsLeft -= maxWorkPointsBatch) {
            WorkProcess workProcess = createNew(
                gameData,
                processId,
                location,
                null,
                SkillType.BUILDING,
                Math.min(workPointsLeft, maxWorkPointsBatch),
                WorkProcessType.DECONSTRUCTION,
                deconstructionId
            );
            log.info("{} created.", workProcess);
            result.add(workProcess);
        }

        return result;
    }

    public List<WorkProcess> createForTerraformation(GameData gameData, UUID processId, UUID constructionId, UUID location, int requiredWorkPoints) {
        int maxWorkPointsBatch = gameProperties.getCitizen()
            .getMaxWorkPointsBatch();

        List<WorkProcess> result = new ArrayList<>();

        for (int workPointsLeft = requiredWorkPoints; workPointsLeft > 0; workPointsLeft -= maxWorkPointsBatch) {
            WorkProcess workProcess = createNew(
                gameData,
                processId,
                location,
                null,
                SkillType.BUILDING,
                Math.min(workPointsLeft, maxWorkPointsBatch),
                WorkProcessType.TERRAFORMATION,
                constructionId
            );
            log.info("{} created.", workProcess);
            result.add(workProcess);
        }

        return result;
    }

    public List<WorkProcess> createForConstruction(GameData gameData, UUID processId, UUID constructionId, UUID location, int requiredWorkPoints) {
        int maxWorkPointsBatch = gameProperties.getCitizen()
            .getMaxWorkPointsBatch();

        List<WorkProcess> result = new ArrayList<>();

        for (int workPointsLeft = requiredWorkPoints; workPointsLeft > 0; workPointsLeft -= maxWorkPointsBatch) {
            WorkProcess workProcess = createNew(
                gameData,
                processId,
                location,
                null,
                SkillType.BUILDING,
                Math.min(workPointsLeft, maxWorkPointsBatch),
                WorkProcessType.CONSTRUCTION,
                constructionId
            );
            log.info("{} created.", workProcess);
            result.add(workProcess);
        }

        return result;
    }

    public WorkProcess createNew(
        GameData gameData,
        UUID externalReference,
        UUID location,
        String buildingDataId,
        SkillType skillType,
        int requiredWorkPoints,
        WorkProcessType processType,
        UUID targetId
    ) {
        return create(
            idGenerator.randomUuid(),
            externalReference,
            gameData,
            location,
            ProcessStatus.CREATED,
            buildingDataId,
            skillType,
            requiredWorkPoints,
            processType,
            targetId,
            0
        );
    }

    private WorkProcess create(
        UUID processId,
        UUID externalReference,
        GameData gameData,
        UUID location,
        ProcessStatus status,
        String buildingDataId,
        SkillType skillType,
        int requiredWorkPoints,
        WorkProcessType workProcessType,
        UUID targetId,
        int completedWorkPoints
    ) {
        return WorkProcess.builder()
            .processId(processId)
            .status(status)
            .externalReference(externalReference)
            .gameData(gameData)
            .location(location)
            .buildingDataId(buildingDataId)
            .skillType(skillType)
            .requiredWorkPoints(requiredWorkPoints)
            .workProcessType(workProcessType)
            .targetId(targetId)
            .applicationContextProxy(applicationContextProxy)
            .completedWorkPoints(completedWorkPoints)
            .build();
    }
}
