package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
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
    //TODO unit test
    public void deleteByUserId(UUID userId) {
        repository.deleteById(uuidConverter.convertDomain(userId));
    }

    //TODO unit test
    public boolean exists(UUID userId) {
        return repository.existsById(uuidConverter.convertDomain(userId));
    }

    //TODO unit test
    public Optional<SkyXploreCharacter> findByName(String name) {
        return converter.convertEntity(repository.findByName(name));
    }

    //TODO unit test
    public Optional<SkyXploreCharacter> findById(UUID userId) {
        return findById(uuidConverter.convertDomain(userId));
    }

    //TODO unit test
    public SkyXploreCharacter findByIdValidated(UUID userId) {
        return findById(userId)
            .orElseThrow(() -> new NotFoundException("Character not found with id " + userId));
    }

    //TODO unit test
    public List<SkyXploreCharacter> getByNameLike(String characterName) {
        return converter.convertEntity(repository.getByNameContainingIgnoreCase(characterName));
    }
}