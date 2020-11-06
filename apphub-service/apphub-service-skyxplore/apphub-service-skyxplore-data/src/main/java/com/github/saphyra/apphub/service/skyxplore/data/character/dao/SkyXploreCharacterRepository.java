package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SkyXploreCharacterRepository extends CrudRepository<SkyXploreCharacterEntity, String> {
    //TODO unit test
    Optional<SkyXploreCharacterEntity> findByName(String name);

    //TODO unit test
    List<SkyXploreCharacterEntity> getByNameContainingIgnoreCase(String characterName);
}
