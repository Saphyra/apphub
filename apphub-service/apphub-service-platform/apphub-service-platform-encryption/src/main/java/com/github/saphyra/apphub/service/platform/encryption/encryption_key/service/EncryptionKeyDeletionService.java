package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.service.SharedDataAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EncryptionKeyDeletionService implements DeleteByUserIdDao {
    private final EncryptionKeyDao encryptionKeyDao;
    private final SharedDataAccessService sharedDataAccessService;
    private final SharedDataDao sharedDataDao;

    @Transactional
    public void deleteEncryptionKey(UUID userId, DataType dataType, UUID externalId, AccessMode accessMode) {
        ValidationUtil.contains(accessMode, List.of(AccessMode.EDIT, AccessMode.DELETE), "accessMode");

        Optional<EncryptionKey> maybeEncryptionKey = encryptionKeyDao.findById(externalId, dataType);

        if (maybeEncryptionKey.isPresent()) {
            EncryptionKey encryptionKey = maybeEncryptionKey.get();

            if (encryptionKey.getUserId().equals(userId) || hasAccess(userId, externalId, dataType, accessMode)) {
                encryptionKeyDao.delete(encryptionKey);
                sharedDataDao.deleteByExternalIdAndDataType(externalId, dataType);
            } else {
                throw ExceptionFactory.forbiddenOperation(userId + " has no access to " + encryptionKey);
            }
        } else {
            log.info("EncryptionKey not found for {} - {}", dataType, externalId);
        }
    }

    private boolean hasAccess(UUID userId, UUID externalId, DataType dataType, AccessMode accessMode) {
        return sharedDataAccessService.hasAccess(userId, accessMode, externalId, dataType);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        encryptionKeyDao.getByUserId(userId)
            .stream()
            .peek(encryptionKey -> sharedDataDao.deleteByExternalIdAndDataType(encryptionKey.getExternalId(), encryptionKey.getDataType()))
            .forEach(encryptionKeyDao::delete);
    }
}
