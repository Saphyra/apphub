package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.service;

import com.github.saphyra.apphub.api.feature.task_manager.model.organization.OrganizationResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.Alm;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao.PrincipalType;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.Organization;
import com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao.OrganizationDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationQueryService {
    private final AlmDao almDao;
    private final OrganizationDao organizationDao;

    public List<OrganizationResponse> getOrganizationsOfUser(UUID userId) {
        List<UUID> organizationIds = almDao.getByUserIdAndObjectType(userId, ObjectType.ORGANIZATION)
            .stream()
            .map(Alm::getObjectId)
            .toList();

        return organizationDao.getByIds(organizationIds)
            .stream()
            .map(OrganizationQueryService::toResponse)
            .toList();
    }

    public OrganizationResponse getOrganization(UUID userId, UUID organizationId) {
        return almDao.findForObject(userId, PrincipalType.USER, organizationId, ObjectType.ORGANIZATION)
            .map(alm -> organizationDao.findByIdValidated(alm.getObjectId()))
            .map(OrganizationQueryService::toResponse)
            .orElseThrow(() -> ExceptionFactory.forbiddenOperation(userId + " has no access to organization " + organizationId));
    }

    private static OrganizationResponse toResponse(Organization organization) {
        return OrganizationResponse.builder()
            .organizationId(organization.getId())
            .organizationName(organization.getName())
            .description(organization.getDescription())
            .build();
    }
}
