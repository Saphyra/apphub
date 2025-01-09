package com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface StarSystemRepository extends CrudRepository<StarSystemEntity, String> {
    Optional<StarSystemEntity> findByStarId(Long starId);

    Optional<StarSystemEntity> findByStarName(String starName);
}
