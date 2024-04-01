package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.lib.common_domain.DeleteByUserIdDao;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EncryptionKeyDeletionService implements DeleteByUserIdDao {
    private final EncryptionKeyDao encryptionKeyDao;
    private final SharedDataDao sharedDataDao;

    @Transactional
    public void deleteEncryptionKey(DataType dataType, UUID externalId) {
        encryptionKeyDao.delete(externalId, dataType);
        sharedDataDao.deleteByExternalIdAndDataType(externalId, dataType);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        encryptionKeyDao.getByUserId(userId)
            .stream()
            .peek(encryptionKey -> sharedDataDao.deleteByExternalIdAndDataType(encryptionKey.getExternalId(), encryptionKey.getDataType()))
            .forEach(encryptionKeyDao::delete);
    }
}
