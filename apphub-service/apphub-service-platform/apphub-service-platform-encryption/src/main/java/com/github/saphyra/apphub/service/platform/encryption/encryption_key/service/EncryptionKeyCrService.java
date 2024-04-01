package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EncryptionKeyCrService {
    private final EncryptionKeyRequestValidator encryptionKeyRequestValidator;
    private final IdGenerator idGenerator;
    private final EncryptionKeyDao encryptionKeyDao;

    public EncryptionKey createEncryptionKey(EncryptionKey request) {
        encryptionKeyRequestValidator.validate(request);

        Optional<EncryptionKey> maybeEncryptionKey = encryptionKeyDao.findById(request.getExternalId(), request.getDataType());

        if (maybeEncryptionKey.isPresent()) {
            log.info("{} already exists.", request);
            return maybeEncryptionKey.get();
        }

        log.info("Encryption key is not found for externalId {} and dataType {}", request.getExternalId(), request.getDataType());

        String encryptionKey = idGenerator.generateRandomId();
        request.setEncryptionKey(encryptionKey);

        encryptionKeyDao.save(request);
        log.info("{} is saved.", request);

        return request;
    }
}
