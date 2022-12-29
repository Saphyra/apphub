package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EncryptionKeyQueryService {
    private final EncryptionKeyDao encryptionKeyDao;

    public Optional<String> getEncryptionKey(UUID userId, DataType dataType, UUID externalId, AccessMode accessMode) {
        Optional<EncryptionKey> maybeEncryptionKey = encryptionKeyDao.findById(externalId, dataType);

        if (maybeEncryptionKey.isPresent()) {
            EncryptionKey encryptionKey = maybeEncryptionKey.get();

            if (encryptionKey.getUserId().equals(userId) || hasReadAccess(userId, encryptionKey, accessMode)) {
                return Optional.of(encryptionKey.getEncryptionKey());
            } else {
                throw ExceptionFactory.forbiddenOperation(userId + " has no access to " + encryptionKey);
            }
        }

        log.info("EncryptionKey not found for {} - {}", dataType, externalId);
        return Optional.empty();
    }

    private boolean hasReadAccess(UUID userId, EncryptionKey encryptionKey, AccessMode accessMode) {
        //TODO implement
        return false;
    }
}
