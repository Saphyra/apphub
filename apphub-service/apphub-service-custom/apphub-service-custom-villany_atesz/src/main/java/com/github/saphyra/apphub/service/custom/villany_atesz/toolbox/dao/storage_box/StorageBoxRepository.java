package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StorageBoxRepository extends CrudRepository<StorageBoxEntity, String> {
    void deleteByUserId(String userId);

    List<StorageBoxEntity> getByUserId(String userId);

    void deleteByUserIdAndStorageBoxId(String userId, String storageBoxId);
}
