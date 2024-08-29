package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.storage_box;

import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StorageBoxDao extends AbstractDao<StorageBoxEntity, StorageBox, String, StorageBoxRepository> implements DeleteByUserIdDao {
    private final UuidConverter uuidConverter;

    StorageBoxDao(StorageBoxConverter converter, StorageBoxRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    @Override
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(uuidConverter.convertDomain(userId));
    }

    public StorageBox findByIdValidated(UUID storageBoxId) {
        return findById(storageBoxId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "StorageBox not found by id " + storageBoxId));
    }

    public Optional<StorageBox> findById(UUID storageBoxId) {
        return findById(uuidConverter.convertDomain(storageBoxId));
    }

    public boolean exists(UUID storageBoxId) {
        return repository.existsById(uuidConverter.convertDomain(storageBoxId));
    }

    public List<StorageBox> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }

    public void deleteByUserIdAndStorageBoxId(UUID userId, UUID storageBoxId) {
        repository.deleteByUserIdAndStorageBoxId(uuidConverter.convertDomain(userId), uuidConverter.convertDomain(storageBoxId));
    }
}
