package com.github.saphyra.apphub.service.platform.encryption.encryption_key;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.api.platform.encryption.server.EncryptionKeyApiController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyCreationService;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyDeletionService;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EncryptionKeyControllerImpl implements EncryptionKeyApiController {
    private final EncryptionKeyQueryService encryptionKeyQueryService;
    private final EncryptionKeyCreationService encryptionKeyCreationService;
    private final EncryptionKeyDeletionService encryptionKeyDeletionService;

    @Override
    public String createEncryptionKey(EncryptionKey request, AccessMode accessMode, AccessToken accessToken) {
        log.info("{} wants to create an encryptionKey: {} with accessMode {}", accessToken.getUserId(), request, accessMode);
        return encryptionKeyCreationService.createEncryptionKey(accessToken.getUserId(), request, accessMode);
    }

    @Override
    public void deleteEncryptionKey(DataType dataType, UUID externalId, AccessMode accessMode, AccessToken accessToken) {
        log.info("{} wants to delete encryptionKey of {} - {} with accessMode {}", accessToken.getUserId(), dataType, externalId, accessMode);
        encryptionKeyDeletionService.deleteEncryptionKey(accessToken.getUserId(), dataType, externalId, accessMode);
    }

    @Override
    public String getEncryptionKey(DataType dataType, UUID externalId, AccessMode accessMode, AccessToken accessToken) {
        log.info("{} wants to query encryptionKey of {} - {} with accessMode {}", accessToken.getUserId(), dataType, externalId, accessMode);

        return encryptionKeyQueryService.getEncryptionKey(accessToken.getUserId(), dataType, externalId, accessMode)
            .orElse(null);
    }
}
