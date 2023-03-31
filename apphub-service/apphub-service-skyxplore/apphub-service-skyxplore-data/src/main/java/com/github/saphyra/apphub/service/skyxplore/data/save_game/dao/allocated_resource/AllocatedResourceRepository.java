package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.allocated_resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface AllocatedResourceRepository extends CrudRepository<AllocatedResourceEntity, String> {
    @Modifying
    @Query("DELETE FROM AllocatedResourceEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<AllocatedResourceEntity> getByLocation(String location);

    //TODO unit test
    List<AllocatedResourceEntity> getByGameId(String gameId, PageRequest pageRequest);
}
