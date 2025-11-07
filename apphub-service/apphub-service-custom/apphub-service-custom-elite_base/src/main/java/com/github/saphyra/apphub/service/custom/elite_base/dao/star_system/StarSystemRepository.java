package com.github.saphyra.apphub.service.custom.elite_base.dao.star_system;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface StarSystemRepository extends CrudRepository<StarSystemEntity, String> {
    Optional<StarSystemEntity> findByStarId(Long starId);

    Optional<StarSystemEntity> findByStarName(String starName);

    @Query(nativeQuery = true, value = "SELECT * FROM elite_base.star_system WHERE LOWER(star_name) LIKE LOWER(CONCAT('%', :query, '%')) LIMIT 10")
    List<StarSystemEntity> getByStarNameIgnoreCaseContaining(String query);

    List<StarSystemEntity> getByStarIdOrStarName(Long starId, String starName);

    @Override
    @Query("DELETE FROM StarSystemEntity s WHERE s.id in :ids")
    @Modifying
    @Transactional
    void deleteAllById(@Param("ids") Iterable<? extends String> ids);
}
