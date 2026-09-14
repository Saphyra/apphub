package com.github.saphyra.apphub.service.feature.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.CachedDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.google.common.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StarSystemDao extends CachedDao<StarSystemEntity, StarSystem, String, StarSystemRepository> {
    private final UuidConverter uuidConverter;

    StarSystemDao(
        StarSystemConverter converter,
        StarSystemRepository repository,
        Cache<String, StarSystem> starSystemReadCache,
        UuidConverter uuidConverter
    ) {
        super(converter, repository, false, starSystemReadCache);
        this.uuidConverter = uuidConverter;
    }

    public Optional<StarSystem> findByStarName(String starName) {
        return cache.asMap()
            .values()
            .stream()
            .filter(starSystem -> starSystem.getStarName().equals(starName))
            .findAny()
            .or(() -> converter.convertEntity(repository.findByStarName(starName)));
    }

    public List<StarSystem> getByStarNameLike(String query) {
        return converter.convertEntity(repository.getByStarNameIgnoreCaseContaining(query));
    }

    public StarSystem findByIdValidated(UUID id) {
        return findById(id)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StarSystem not found by id " + id));
    }

    private Optional<StarSystem> findById(UUID id) {
        return findById(uuidConverter.convertDomain(id));
    }

    public List<StarSystem> findAllById(List<UUID> starIds) {
        return super.findAllById(uuidConverter.convertDomain(starIds));
    }

    public List<StarSystem> getByIds(Collection<UUID> ids) {
        return findAllById(uuidConverter.convertDomain(ids));
    }

    @Override
    protected String extractId(StarSystem starSystem) {
        return uuidConverter.convertDomain(starSystem.getId());
    }

    @Override
    protected boolean shouldSave(StarSystem starSystem) {
        return true;
    }
}
