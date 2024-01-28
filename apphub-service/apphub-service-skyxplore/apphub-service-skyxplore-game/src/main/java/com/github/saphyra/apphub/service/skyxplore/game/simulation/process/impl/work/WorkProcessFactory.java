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
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.DeconstructionProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkProcessFactory implements ProcessFactory {
    private final ProductionBuildingService productionBuildingService;
    private final GameProperties gameProperties;
    private final UuidConverter uuidConverter;
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;
    private final GameDao gameDao;
    private final HeadquartersUtil headquartersUtil;

    @Override
    public ProcessType getType() {
        return ProcessType.WORK;
    }

    @Override
    public WorkProcess createFromModel(Game game, ProcessModel model) {
        return create(
            model.getId(),
            model.getExternalReference(),
            game,
            model.getLocation(),
            model.getStatus(),
            model.getData().get(ProcessParamKeys.BUILDING_DATA_ID),
            SkillType.valueOf(model.getData().get(ProcessParamKeys.SKILL_TYPE)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.REQUIRED_WORK_POINTS)),
            WorkProcessType.valueOf(model.getData().get(ProcessParamKeys.WORK_PROCESS_TYPE)),
            uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.TARGET_ID)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.COMPLETED_WORK_POINTS))
        );
    }

    public List<WorkProcess> createForProduction(GameData gameData, UUID processId, UUID location, String producerBuildingDataId, String dataId, Integer amount) {
        log.info("Creating WorkPointProcesses...");

        ProductionData productionData = Optional.ofNullable(productionBuildingService.get(producerBuildingDataId))
            .map(productionBuildingData -> productionBuildingData.getGives().get(dataId))
            .orElseGet(() -> headquartersUtil.getProductionData(dataId));

        ConstructionRequirements constructionRequirements = productionData.getConstructionRequirements();
        int requiredWorkPoints = amount * constructionRequirements.getRequiredWorkPoints();

        return createNews(gameData, processId, location, producerBuildingDataId, productionData.getRequiredSkill(), requiredWorkPoints, WorkProcessType.OTHER, null);
    }

    public List<WorkProcess> createForDeconstruction(GameData gameData, UUID processId, UUID deconstructionId, UUID location) {
        DeconstructionProperties deconstructionProperties = gameProperties.getDeconstruction();

        return createNews(gameData, processId, location, null, SkillType.BUILDING, deconstructionProperties.getRequiredWorkPoints(), WorkProcessType.DECONSTRUCTION, deconstructionId);
    }

    public List<WorkProcess> createForTerraformation(GameData gameData, UUID processId, UUID constructionId, UUID location, int requiredWorkPoints) {
        return createNews(gameData, processId, location, null, SkillType.BUILDING, requiredWorkPoints, WorkProcessType.TERRAFORMATION, constructionId);
    }

    public List<WorkProcess> createForConstruction(GameData gameData, UUID processId, UUID constructionId, UUID location, int requiredWorkPoints) {
        return createNews(gameData, processId, location, null, SkillType.BUILDING, requiredWorkPoints, WorkProcessType.CONSTRUCTION, constructionId);
    }

    private List<WorkProcess> createNews(
        GameData gameData,
        UUID externalReference,
        UUID location,
        String buildingDataId,
        SkillType skillType,
        int requiredWorkPoints,
        WorkProcessType processType,
        UUID targetId
    ) {
        int maxWorkPointsBatch = gameProperties.getCitizen()
            .getMaxWorkPointsBatch();

        List<WorkProcess> result = new ArrayList<>();

        for (int workPointsLeft = requiredWorkPoints; workPointsLeft > 0; workPointsLeft -= maxWorkPointsBatch) {
            WorkProcess workProcess = createNew(
                gameData,
                externalReference,
                location,
                buildingDataId,
                skillType,
                Math.min(workPointsLeft, maxWorkPointsBatch),
                processType,
                targetId
            );
            log.info("{} created.", workProcess);
            result.add(workProcess);
        }

        return result;
    }

    private WorkProcess createNew(
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
            gameDao.findById(gameData.getGameId()),
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
        Game game,
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
            .gameData(game.getData())
            .location(location)
            .buildingDataId(buildingDataId)
            .skillType(skillType)
            .requiredWorkPoints(requiredWorkPoints)
            .workProcessType(workProcessType)
            .targetId(targetId)
            .applicationContextProxy(applicationContextProxy)
            .completedWorkPoints(completedWorkPoints)
            .game(game)
            .build();
    }
}
