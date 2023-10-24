package com.github.saphyra.apphub.api.admin_panel.server;

import com.github.saphyra.apphub.api.admin_panel.model.model.MigrationTaskResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

public interface MigrationTaskController {
    @GetMapping(Endpoints.ADMIN_PANEL_MIGRATION_GET_TASKS)
    List<MigrationTaskResponse> getMigrationTasks(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.ADMIN_PANEL_MIGRATION_TRIGGER_TASK)
    List<MigrationTaskResponse> triggerMigrationTask(@PathVariable("event") String event, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.ADMIN_PANEL_MIGRATION_DELETE_TASK)
    List<MigrationTaskResponse> deleteMigrationTask(@PathVariable("event") String event, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
