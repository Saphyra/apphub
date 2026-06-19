package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
//TODO unit test
public class OrganizationDao {
    private final List<Organization> repository = Collections.synchronizedList(new ArrayList<>());

    public void save(Organization organization) {
        repository.add(organization);
    }

    public List<Organization> getByIds(List<UUID> organizationIds) {
        return repository.stream()
            .filter(organization -> organizationIds.contains(organization.getId()))
            .toList();
    }

    public Organization findByIdValidated(UUID organizationId) {
        return repository.stream()
            .filter(organization -> organization.getId().equals(organizationId))
            .findAny()
            .orElseThrow(() -> ExceptionFactory.notFound("Organization not found by id " + organizationId));
    }
}
