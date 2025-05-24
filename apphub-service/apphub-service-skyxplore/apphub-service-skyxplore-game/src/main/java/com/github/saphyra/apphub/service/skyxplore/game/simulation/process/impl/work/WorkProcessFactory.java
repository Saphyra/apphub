package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessStatus;
import com.github.saphyra.apphub.api.skyxplore.model.game.ProcessType;
import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessFactory;
import com.github.saphyra.apphub.service.skyxplore.game.simulation.process.ProcessParamKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class WorkProcessFactory implements ProcessFactory {
    private final GameProperties gameProperties;
    private final ApplicationContextProxy applicationContextProxy;
    private final IdGenerator idGenerator;

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
            SkillType.valueOf(model.getData().get(ProcessParamKeys.SKILL_TYPE)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.REQUIRED_WORK_POINTS)),
            Integer.parseInt(model.getData().get(ProcessParamKeys.COMPLETED_WORK_POINTS))
        );
    }

    public void save(Game game, UUID location, UUID externalReference, int workPointsNeeded, SkillType skillType) {
        int maxWorkPointsPerBatch = gameProperties.getCitizen()
            .getMaxWorkPointsBatch();

        while (workPointsNeeded > 0) {
            int workPoints = Math.min(maxWorkPointsPerBatch, workPointsNeeded);

            WorkProcess process = create(
                idGenerator.randomUuid(),
                externalReference,
                game,
                location,
                ProcessStatus.CREATED,
                skillType,
                workPoints,
                0
            );

            game.getData()
                .getProcesses()
                .add(process);
            game.getProgressDiff()
                .save(process.toModel());

            workPointsNeeded -= workPoints;
        }
    }

    private WorkProcess create(
        UUID processId,
        UUID externalReference,
        Game game,
        UUID location,
        ProcessStatus status,
        SkillType skillType,
        int requiredWorkPoints,
        int completedWorkPoints
    ) {
        return WorkProcess.builder()
            .processId(processId)
            .status(status)
            .externalReference(externalReference)
            .location(location)
            .skillType(skillType)
            .requiredWorkPoints(requiredWorkPoints)
            .applicationContextProxy(applicationContextProxy)
            .completedWorkPoints(completedWorkPoints)
            .game(game)
            .build();
    }
}
