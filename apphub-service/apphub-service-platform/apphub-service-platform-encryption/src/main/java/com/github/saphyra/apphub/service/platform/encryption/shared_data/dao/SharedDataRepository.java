package com.github.saphyra.apphub.service.platform.encryption.shared_data.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface SharedDataRepository extends CrudRepository<SharedDataEntity, String> {
    List<SharedDataEntity> getByExternalIdAndDataTypeAndAccessMode(String externalId, String dataType, String accessMode);

    List<SharedDataEntity> getByExternalIdAndDataType(String externalId, String dataType);

    void deleteByExternalIdAndDataType(String externalId, String dataType);
}
