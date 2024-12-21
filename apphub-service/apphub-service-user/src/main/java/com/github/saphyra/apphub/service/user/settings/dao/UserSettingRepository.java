package com.github.saphyra.apphub.service.user.settings.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSettingRepository extends CrudRepository<UserSettingEntity, UserSettingEntityId> {
    @Query("SELECT e FROM UserSettingEntity e WHERE e.id.userId = :userId AND e.id.category = :category")
    List<UserSettingEntity> getByUserIdAndCategory(@Param("userId") String userId, @Param("category") String category);

    @Query("DELETE FROM UserSettingEntity e WHERE e.id.userId = :userId")
    @Modifying
    void deleteByUserId(@Param("userId") String userId);
}
