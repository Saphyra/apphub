package com.github.saphyra.apphub.service.platform.encryption.encryption_key;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.api.platform.encryption.server.EncryptionKeyApiController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyCrService;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyDeletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EncryptionKeyControllerImpl implements EncryptionKeyApiController {
    private final EncryptionKeyCrService encryptionKeyCrService;
    private final EncryptionKeyDeletionService encryptionKeyDeletionService;

    @Override
    public EncryptionKey getOrCreateEncryptionKey(EncryptionKey request, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to get or create an encryptionKey: {}", accessTokenHeader.getUserId(), request);
        return encryptionKeyCrService.createEncryptionKey(request);
    }

    @Override
    public void deleteEncryptionKey(DataType dataType, UUID externalId, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to delete encryptionKey of {} - {}", accessTokenHeader.getUserId(), dataType, externalId);
        encryptionKeyDeletionService.deleteEncryptionKey(dataType, externalId);
    }
}
