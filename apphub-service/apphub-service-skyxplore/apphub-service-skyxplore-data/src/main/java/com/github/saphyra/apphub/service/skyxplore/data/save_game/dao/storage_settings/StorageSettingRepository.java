package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface StorageSettingRepository extends CrudRepository<StorageSettingEntity, String> {
    @Modifying
    @Query("DELETE FROM StorageSettingEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);

    List<StorageSettingEntity> getByLocation(String location);

    //TODO unit test
    List<StorageSettingEntity> getByGameId(String gameId, PageRequest pageRequest);
}
