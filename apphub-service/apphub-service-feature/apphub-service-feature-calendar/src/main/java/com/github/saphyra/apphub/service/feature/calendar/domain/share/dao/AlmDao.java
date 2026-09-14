package com.github.saphyra.apphub.service.feature.calendar.domain.share.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlmDao implements DeleteByUserIdDao {
    private final AlmRepository repository;
    private final PrincipalAlmCache principalAlmCache;
    private final ObjectAlmCache objectAlmCache;

    public void save(Alm alm) {
        repository.save(alm);
        principalAlmCache.invalidate(alm.getPrincipal());
        objectAlmCache.invalidate(alm.getObjectId());
    }

    public Map<UUID, Alm> getByUserId(UUID userId) {
        return principalAlmCache.get(
            userId,
            () -> repository.getByPrincipal(userId, PrincipalType.USER)
                .stream()
                .collect(Collectors.toMap(Alm::getObjectId, alm -> alm))
        );
    }

    public List<Alm> getByUserIdAndObjectType(UUID userId, SharedObjectType objectType) {
        return getByUserId(userId)
            .values()
            .stream()
            .filter(alm -> alm.getObjectType() == objectType)
            .collect(Collectors.toList());
    }

    public Alm findSharedObjectValidated(UUID principalId, PrincipalType principalType, UUID objectId, SharedObjectType type) {
        return findSharedObject(principalId, objectId, type)
            .orElseThrow(() -> ExceptionFactory.notFound("Alm not found for %s %s and %s %s".formatted(principalType, principalId, type, objectId)));
    }

    public Optional<Alm> findSharedObject(UUID principalId, UUID objectId, SharedObjectType objectType) {
        return Optional.ofNullable(getByUserId(principalId).get(objectId));
    }

    public Map<UUID, Alm> getByObjectId(UUID objectId, SharedObjectType objectType) {
        return objectAlmCache.get(
            objectId,
            () -> repository.getForObjectId(objectId, objectType)
                .stream()
                .collect(Collectors.toMap(Alm::getPrincipal, alm -> alm))
        );
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, Alm> alms = getByUserId(userId);
        repository.delete(alms.values());
        principalAlmCache.invalidate(userId);

        objectAlmCache.invalidateAll(alms.keySet());
    }

    public void deleteByObject(UUID objectId, SharedObjectType objectType) {
        log.info("Deleting Alms for {} {}", objectType, objectId);
        Map<UUID, Alm> alms = getByObjectId(objectId, objectType);
        repository.delete(alms.values());
        objectAlmCache.invalidate(objectId);

        principalAlmCache.invalidateAll(alms.keySet());
    }

    public void delete(Alm alm) {
        repository.delete(List.of(alm));
        principalAlmCache.invalidate(alm.getPrincipal());
        objectAlmCache.invalidate(alm.getObjectId());
    }
}
