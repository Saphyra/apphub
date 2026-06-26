package com.github.saphyra.apphub.service.feature.task_manager.domain.organization;

import com.github.saphyra.apphub.api.feature.task_manager.model.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.api.feature.task_manager.model.organization.OrganizationResponse;
import com.github.saphyra.apphub.api.feature.task_manager.server.OrganizationController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service.CreateOrganizationService;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service.OrganizationQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
class OrganizationControllerImpl implements OrganizationController {
    private final CreateOrganizationService createOrganizationService;
    private final OrganizationQueryService organizationQueryService;

    @Override
    public void createOrganization(CreateOrganizationRequest request, AccessToken accessToken) {
        log.info("{} wants to create an organization with name {}", accessToken.getUserId(), request.getOrganizationName());

        createOrganizationService.createOrganization(accessToken.getUserId(), request);
    }

    @Override
    public List<OrganizationResponse> getOrganizations(AccessToken accessToken) {
        log.info("{} wants to know their organizations", accessToken.getUserId());

        return organizationQueryService.getOrganizationsOfUser(accessToken.getUserId());
    }

    @Override
    public OrganizationResponse getOrganization(UUID organizationId, AccessToken accessToken) {
        log.info("{} wants to query organization {}", accessToken.getUserId(), organizationId);

        return organizationQueryService.getOrganization(accessToken.getUserId(), organizationId);
    }
}
