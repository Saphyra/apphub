package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.reserved_storage;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

interface ReservedStorageRepository extends CrudRepository<ReservedStorageEntity, String> {
    @Modifying
    @Query("DELETE FROM ReservedStorageEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<ReservedStorageEntity> getByLocation(String location);
}
