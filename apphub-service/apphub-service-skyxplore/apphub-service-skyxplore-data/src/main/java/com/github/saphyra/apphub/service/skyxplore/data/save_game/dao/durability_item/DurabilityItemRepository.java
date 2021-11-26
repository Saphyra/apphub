package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.durability_item;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface DurabilityItemRepository extends CrudRepository<DurabilityItemEntity, String> {
    @Modifying
    @Query("DELETE FROM DurabilityItemEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<DurabilityItemEntity> getByParent(String parent);
}
