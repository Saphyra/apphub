package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.production_order;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ProductionOrderRepository extends CrudRepository<ProductionOrderEntity, String> {
    @Modifying
    @Query("DELETE FROM ProductionOrderEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<ProductionOrderEntity> getByLocation(String location);
}
