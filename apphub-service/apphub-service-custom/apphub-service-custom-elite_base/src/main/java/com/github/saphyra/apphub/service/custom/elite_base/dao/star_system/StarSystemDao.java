package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.dao.CachedBufferedDao;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import com.google.common.cache.Cache;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
public class StarSystemDao extends CachedBufferedDao<StarSystemEntity, StarSystem, String, UUID, StarSystemRepository> {
    private static final BiFunction<StarSystem, StarSystem, StarSystem> MERGER = (s1, s2) -> {
        if (s1.getLastUpdate().equals(s2.getLastUpdate())) {
            return s1;
        } else if (s1.getLastUpdate().isAfter(s2.getLastUpdate())) {
            return s1;
        } else if (s2.getLastUpdate().isAfter(s1.getLastUpdate())) {
            return s2;
        }

        throw new IllegalStateException("Cannot merge " + s1 + " with " + s2);
    };

    private final UuidConverter uuidConverter;
    private final EliteBaseProperties properties;

    protected StarSystemDao(
        StarSystemConverter converter,
        StarSystemRepository repository,
        Cache<UUID, StarSystem> starSystemReadCache,
        StarSystemWriteBuffer writeBuffer,
        StarSystemDeleteBuffer deleteBuffer,
        UuidConverter uuidConverter,
        EliteBaseProperties properties
    ) {
        super(
            converter,
            repository,
            starSystemReadCache,
            writeBuffer,
            deleteBuffer
        );
        this.uuidConverter = uuidConverter;
        this.properties = properties;
    }

    public Optional<StarSystem> findByStarName(String starName) {
        return searchOne(starSystem -> starSystem.getStarName().equals(starName), () -> repository.findByStarName(starName), MERGER);
    }

    public List<StarSystem> getByStarNameLike(String query) {
        return converter.convertEntity(repository.getByStarNameIgnoreCaseContaining(query, PageRequest.of(0, properties.getStarSystemSuggestionListSize())));
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

    @Override
    protected UUID getCacheKey(StarSystem starSystem) {
        return starSystem.getId();
    }

    @Override
    protected UUID toCacheKey(String id) {
        return uuidConverter.convertEntity(id);
    }
}
