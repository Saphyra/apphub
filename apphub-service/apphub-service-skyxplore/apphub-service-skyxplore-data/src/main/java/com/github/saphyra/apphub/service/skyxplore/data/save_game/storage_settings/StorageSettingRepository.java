package com.github.saphyra.apphub.service.skyxplore.data.save_game.storage_settings;

import org.springframework.data.repository.CrudRepository;

//TODO unit test
interface StorageSettingRepository extends CrudRepository<StorageSettingEntity, String> {
    void deleteByGameId(String gameId);
}
