package com.github.saphyra.apphub.service.feature.task_manager.domain.organization.dao;

import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationDao {
    private final OrganizationRepository repository;

    public void save(Organization organization) {
        repository.save(organization);
    }

    public List<Organization> getByIds(List<UUID> organizationIds) {
        return repository.getByIds(organizationIds);
    }

    public Organization findByIdValidated(UUID organizationId) {
        return findById(organizationId)
            .orElseThrow(() -> ExceptionFactory.notFound("Organization not found by id " + organizationId));
    }

    public Optional<Organization> findById(UUID organizationId) {
        return repository.findById(organizationId);
    }

    public void delete(UUID organizationId) {
        repository.delete(organizationId);
    }
}