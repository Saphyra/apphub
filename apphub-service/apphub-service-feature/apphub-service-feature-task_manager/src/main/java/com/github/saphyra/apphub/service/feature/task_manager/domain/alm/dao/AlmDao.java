package com.github.saphyra.apphub.service.feature.task_manager.domain.alm.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AlmDao implements DeleteByUserIdDao {
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

    public List<Alm> getByObjects(List<UUID> objectIds, ObjectType objectType) {
        List<BiWrapper<UUID, ObjectType>> keys = objectIds.stream()
            .map(objectId -> new BiWrapper<>(objectId, objectType))
            .toList();

        return repository.getByObjects(keys);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.delete(repository.getByPrincipal(userId, PrincipalType.USER));
    }

    public void deleteByObject(UUID organizationId, ObjectType objectType) {
        repository.delete(repository.getForObject(organizationId, objectType));
    }
}
