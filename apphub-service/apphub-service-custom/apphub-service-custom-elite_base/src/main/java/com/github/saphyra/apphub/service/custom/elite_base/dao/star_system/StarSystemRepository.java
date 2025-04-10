package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface StarSystemRepository extends CrudRepository<StarSystemEntity, String> {
    Optional<StarSystemEntity> findByStarId(Long starId);

    Optional<StarSystemEntity> findByStarName(String starName);

    List<StarSystemEntity> getByStarNameIgnoreCaseContaining(String query, PageRequest pageRequest);

    List<StarSystemEntity> getByStarIdOrStarName(Long starId, String starName);
}
