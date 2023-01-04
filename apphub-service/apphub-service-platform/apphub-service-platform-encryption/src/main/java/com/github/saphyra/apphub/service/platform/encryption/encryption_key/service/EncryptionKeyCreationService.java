package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EncryptionKeyCreationService {
    private final EncryptionKeyRequestValidator encryptionKeyRequestValidator;
    private final EncryptionKeyQueryService encryptionKeyQueryService;
    private final IdGenerator idGenerator;
    private final EncryptionKeyDao encryptionKeyDao;

    public String createEncryptionKey(UUID userId, EncryptionKey request, AccessMode accessMode) {
        encryptionKeyRequestValidator.validate(request);

        Optional<String> maybeEncryptionKey = encryptionKeyQueryService.getEncryptionKey(userId, request.getDataType(), request.getExternalId(), accessMode);

        if (maybeEncryptionKey.isPresent()) {
            log.info("{} already exists.", request);
            return maybeEncryptionKey.get();
        }

        String encryptionKey = idGenerator.generateRandomId();

        request.setEncryptionKey(encryptionKey);

        encryptionKeyDao.save(request);

        log.info("{} is saved.", request);

        return encryptionKey;
    }
}
