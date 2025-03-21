package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.custom.elite_base.common.EliteBaseProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Component
public class StarSystemDao extends AbstractDao<StarSystemEntity, StarSystem, String, StarSystemRepository> {
    private final UuidConverter uuidConverter;
    private final EliteBaseProperties properties;

    StarSystemDao(StarSystemConverter converter, StarSystemRepository repository, UuidConverter uuidConverter, EliteBaseProperties properties) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
        this.properties = properties;
    }

    public Optional<StarSystem> findByStarId(Long starId) {
        return converter.convertEntity(repository.findByStarId(starId));
    }

    public Optional<StarSystem> findByStarName(String starName) {
        return converter.convertEntity(repository.findByStarName(starName));
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
        List<StarSystemEntity> entities = StreamSupport.stream(repository.findAllById(uuidConverter.convertDomain(starIds)).spliterator(), false)
            .toList();

        return converter.convertEntity(entities);
    }
}
