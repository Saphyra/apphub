package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlmDao {
    private final AlmRepository repository;

    public void save(Alm alm) {
        repository.save(alm);
    }

    public List<Alm> getByUserIdAndObjectType(UUID userId, ObjectType objectType) {
        return repository.getByPrincipalAndObjectType(userId, PrincipalType.USER, objectType);
    }

    public Optional<Alm> findForObject(UUID principalId, PrincipalType principalType, UUID objectId, ObjectType objectType) {
        return repository.findForObject(principalId, principalType, objectId, objectType);
    }
}
