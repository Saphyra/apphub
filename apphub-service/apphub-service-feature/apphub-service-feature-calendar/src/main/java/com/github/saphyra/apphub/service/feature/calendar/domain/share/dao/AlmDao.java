package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlmDao implements DeleteByUserIdDao {
    private final AlmRepository repository;

    public void save(Alm alm) {
        repository.save(alm);
    }

    public List<Alm> getByUserIdAndObjectType(UUID userId, SharedObjectType objectType) {
        return repository.getByPrincipalAndObjectType(userId, PrincipalType.USER, objectType);
    }

    public Alm findForObjectValidated(UUID principalId, PrincipalType principalType, UUID objectId, SharedObjectType type) {
        return findForObject(principalId, principalType, objectId, type)
            .orElseThrow(() -> ExceptionFactory.notFound("Alm not found for %s %s and %s %s".formatted(principalType, principalId, type, objectId)));
    }

    public Optional<Alm> findForObject(UUID principalId, PrincipalType principalType, UUID objectId, SharedObjectType objectType) {
        return repository.findForObject(principalId, principalType, objectId, objectType);
    }

    public List<Alm> getByObject(UUID objectId, SharedObjectType objectType) {
        return repository.getForObject(objectId, objectType);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.delete(repository.getByPrincipal(userId, PrincipalType.USER));
    }

    public void deleteByObject(UUID objectId, SharedObjectType objectType) {
        log.info("Deleting Alms for {} {}", objectType, objectId);
        repository.delete(repository.getForObject(objectId, objectType));
    }

    public void delete(Alm alm) {
        repository.delete(List.of(alm));
    }
}
