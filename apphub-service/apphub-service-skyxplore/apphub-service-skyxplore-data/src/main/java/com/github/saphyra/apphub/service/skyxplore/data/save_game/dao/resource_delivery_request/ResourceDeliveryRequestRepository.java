package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.resource_delivery_request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ResourceDeliveryRequestRepository extends CrudRepository<ResourceDeliveryRequestEntity, String> {
    @Query("DELETE FROM ResourceDeliveryRequestEntity e WHERE e.gameId = :gameId")
    @Modifying
    void deleteByGameId(@Param("gameId") String gameId);

    List<ResourceDeliveryRequestEntity> getByGameId(String gameId, PageRequest pageRequest);
}
