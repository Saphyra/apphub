package com.github.saphyra.apphub.api.feature.task_manager.server;

import com.github.saphyra.apphub.api.feature.task_manager.model.TaskManagerEndpoints;
import com.github.saphyra.apphub.api.feature.task_manager.model.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.api.feature.task_manager.model.organization.OrganizationResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

//TODO role protection test
//TODO API test
public interface OrganizationController {
    @PostMapping(TaskManagerEndpoints.TASK_MANAGER_CREATE_ORGANIZATION)
    void createOrganization(@RequestBody CreateOrganizationRequest request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);

    @GetMapping(TaskManagerEndpoints.TASK_MANAGER_GET_ORGANIZATIONS)
    List<OrganizationResponse> getOrganizations(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessToken accessToken);
}
