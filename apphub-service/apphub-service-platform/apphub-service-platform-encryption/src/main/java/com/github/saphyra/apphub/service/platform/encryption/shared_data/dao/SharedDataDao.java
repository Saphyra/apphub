package com.github.saphyra.apphub.service.platform.encryption.shared_data.dao;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.SharedData;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SharedDataDao extends AbstractDao<SharedDataEntity, SharedData, String, SharedDataRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    public SharedDataDao(SharedDataConverter converter, SharedDataRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<SharedData> getByExternalIdAndDataTypeAndAccessMode(UUID externalId, DataType dataType, AccessMode accessMode) {
        return converter.convertEntity(repository.getByExternalIdAndDataTypeAndAccessMode(uuidConverter.convertDomain(externalId), dataType.name(), accessMode.name()));
    }

    public List<SharedData> getByExternalIdAndDataType(UUID externalId, DataType dataType) {
        return converter.convertEntity(repository.getByExternalIdAndDataType(uuidConverter.convertDomain(externalId), dataType.name()));
    }

    public void deleteById(UUID sharedDataId) {
        deleteById(uuidConverter.convertDomain(sharedDataId));
    }

    public void deleteByExternalIdAndDataType(UUID externalId, DataType dataType) {
        repository.deleteByExternalIdAndDataType(uuidConverter.convertDomain(externalId), dataType.name());
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteBySharedWith(uuidConverter.convertDomain(userId));
    }
}
