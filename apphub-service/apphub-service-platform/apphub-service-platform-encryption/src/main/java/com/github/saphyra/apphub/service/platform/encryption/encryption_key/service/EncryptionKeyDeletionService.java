package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.service.SharedDataAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EncryptionKeyDeletionService {
    private final EncryptionKeyDao encryptionKeyDao;
    private final SharedDataAccessService sharedDataAccessService;

    public void deleteEncryptionKey(UUID userId, DataType dataType, UUID externalId, AccessMode accessMode) {
        Optional<EncryptionKey> maybeEncryptionKey = encryptionKeyDao.findById(externalId, dataType);

        ValidationUtil.contains(accessMode, List.of(AccessMode.EDIT, AccessMode.DELETE), "accessMode");

        if (maybeEncryptionKey.isPresent()) {
            EncryptionKey encryptionKey = maybeEncryptionKey.get();

            if (encryptionKey.getUserId().equals(userId) || hasAccess(userId, encryptionKey, accessMode)) {
                encryptionKeyDao.delete(encryptionKey);
            }
        } else {
            log.info("EncryptionKey not found for {} - {}", dataType, externalId);
        }
    }

    private boolean hasAccess(UUID userId, EncryptionKey encryptionKey, AccessMode accessMode) {
        return sharedDataAccessService.hasAccess(userId, accessMode, encryptionKey.getExternalId(), encryptionKey.getDataType());
    }
}
