package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.ApplicationContextProxy;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.process.ProcessParamKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestWorkProcessFactory implements ProcessFactory {
    private final IdGenerator idGenerator;
    private final ApplicationContextProxy applicationContextProxy;
    private final UuidConverter uuidConverter;

    @Override
    public ProcessType getType() {
        return ProcessType.REQUEST_WORK;
    }

    public List<RequestWorkProcess> create(
        GameData gameData,
        UUID externalReference,
        UUID location,
        UUID targetId,
        RequestWorkProcessType processType,
        SkillType skillType,
        int requiredWorkPoints,
        int parallelWorkers
    ) {
        int workPointsPerWorker = requiredWorkPoints / parallelWorkers;
        log.info("WorkPointsPerWorker: {}", workPointsPerWorker);

        List<RequestWorkProcess> result = new ArrayList<>();

        for (int i = 0; i < parallelWorkers; i++) {
            RequestWorkProcess requestWorkProcess = create(
                externalReference,
                gameData,
                location,
                null,
                skillType,
                workPointsPerWorker,
                processType,
                targetId
            );
            log.info("{} created.", requestWorkProcess);
            result.add(requestWorkProcess);
        }
        log.info("RequestWorkProcesses created.");

        return result;
    }

    public RequestWorkProcess create(UUID externalReference, GameData gameData, UUID location, String buildingDataId, SkillType skillType, int requiredWorkPoints) {
        return create(externalReference, gameData, location, buildingDataId, skillType, requiredWorkPoints, RequestWorkProcessType.OTHER, null);
    }

    private RequestWorkProcess create(
        UUID externalReference,
        GameData gameData,
        UUID location,
        String buildingDataId,
        SkillType skillType,
        int requiredWorkPoints,
        RequestWorkProcessType requestWorkProcessType,
        UUID targetId
    ) {
        return create(idGenerator.randomUuid(), externalReference, gameData, location, ProcessStatus.CREATED, buildingDataId, skillType, requiredWorkPoints, requestWorkProcessType, targetId, 0, 0);
    }

    @Override
    public RequestWorkProcess createFromModel(Game game, ProcessModel model) {
        return create(
            model.getId(),
            model.getExternalReference(),
            game.getData(),
            model.getLocation(),
            model.getStatus(),
            model.getData().get(ProcessParamKeys.BUILDING_DATA_ID),
            SkillType.valueOf(model.getData().get(ProcessParamKeys.SKILL_TYPE)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.REQUIRED_WORK_POINTS)),
            RequestWorkProcessType.valueOf(model.getData().get(ProcessParamKeys.REQUEST_WORK_PROCESS_TYPE)),
            uuidConverter.convertEntity(model.getData().get(ProcessParamKeys.TARGET_ID)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.CYCLE)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.COMPLETED_WORK_POINTS))
        );
    }

    private RequestWorkProcess create(
        UUID processId,
        UUID externalReference,
        GameData gameData,
        UUID location,
        ProcessStatus status,
        String buildingDataId,
        SkillType skillType,
        int requiredWorkPoints,
        RequestWorkProcessType requestWorkProcessType,
        UUID targetId,
        int cycle,
        int completedWorkPoints
    ) {
        return RequestWorkProcess.builder()
            .processId(processId)
            .status(status)
            .externalReference(externalReference)
            .gameData(gameData)
            .location(location)
            .buildingDataId(buildingDataId)
            .skillType(skillType)
            .requiredWorkPoints(requiredWorkPoints)
            .requestWorkProcessType(requestWorkProcessType)
            .targetId(targetId)
            .applicationContextProxy(applicationContextProxy)
            .cycle(cycle)
            .completedWorkPoints(completedWorkPoints)
            .build();
    }
}
