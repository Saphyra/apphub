package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CharacterDao extends AbstractDao<SkyXploreCharacterEntity, SkyXploreCharacter, String, SkyXploreCharacterRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public CharacterDao(SkyXploreCharacterConverter converter, SkyXploreCharacterRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        deleteById(uuidConverter.convertDomain(userId));
    }

    public boolean exists(UUID userId) {
        return repository.existsById(uuidConverter.convertDomain(userId));
    }

    public Optional<SkyXploreCharacter> findByName(String name) {
        return converter.convertEntity(repository.findByName(name));
    }

    public Optional<SkyXploreCharacter> findById(UUID userId) {
        return findById(uuidConverter.convertDomain(userId));
    }

    public SkyXploreCharacter findByIdValidated(UUID userId) {
        return findById(userId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.GENERAL_ERROR, "Character not found with id " + userId));
    }

    public List<SkyXploreCharacter> getByNameLike(String characterName) {
        return converter.convertEntity(repository.getByNameContainingIgnoreCase(characterName));
    }
}
