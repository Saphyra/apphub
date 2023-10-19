package com.github.saphyra.apphub.service.admin_panel.migration_task;

import com.github.saphyra.apphub.api.admin_panel.model.model.MigrationTaskResponse;
import com.github.saphyra.apphub.api.admin_panel.server.MigrationTaskController;
import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.admin_panel.migration_task.dao.MigrationTask;
import com.github.saphyra.apphub.service.admin_panel.migration_task.dao.MigrationTaskDao;
import com.github.saphyra.apphub.service.admin_panel.proxy.EventGatewayProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MigrationTaskControllerImpl implements MigrationTaskController {
    private final MigrationTaskDao migrationTaskDao;
    private final EventGatewayProxy eventGatewayProxy;

    @Override
    public List<MigrationTaskResponse> getMigrationTasks(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query the migration tasks", accessTokenHeader.getUserId());
        return migrationTaskDao.findAll()
            .stream()
            .map(migrationTask -> MigrationTaskResponse.builder()
                .event(migrationTask.getEvent())
                .name(migrationTask.getName())
                .completed(migrationTask.getCompleted())
                .build())
            .toList();
    }

    @Override
    public List<MigrationTaskResponse> triggerMigrationTask(String event, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to trigger migration task {}", accessTokenHeader.getUserId(), event);
        MigrationTask migrationTask = migrationTaskDao.findById(event)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "MigrationTask not found with event " + event));

        if (migrationTask.getCompleted()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.GONE, ErrorCode.GENERAL_ERROR, "MigrationTask with event " + event + " is already completed.");
        }

        SendEventRequest<?> sendEventRequest = SendEventRequest.builder()
            .eventName(event)
            .build();
        eventGatewayProxy.sendEvent(sendEventRequest);

        migrationTask.setCompleted(true);
        migrationTaskDao.save(migrationTask);

        return getMigrationTasks(accessTokenHeader);
    }

    @Override
    public List<MigrationTaskResponse> deleteMigrationTask(String event, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete migration task {}", accessTokenHeader.getUserId(), event);

        migrationTaskDao.deleteById(event);

        return getMigrationTasks(accessTokenHeader);
    }
}
