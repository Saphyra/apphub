package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.storage_settings;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

interface StorageSettingRepository extends CrudRepository<StorageSettingEntity, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM StorageSettingEntity e WHERE e.gameId = :gameId")
    void deleteByGameId(@Param("gameId") String gameId);
}
