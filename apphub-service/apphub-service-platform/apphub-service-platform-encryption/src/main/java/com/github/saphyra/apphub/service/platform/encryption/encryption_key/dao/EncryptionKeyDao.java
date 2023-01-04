package com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EncryptionKeyDao extends AbstractDao<EncryptionKeyEntity, EncryptionKey, EncryptionKeyPk, EncryptionKeyRepository> {
    private final UuidConverter uuidConverter;

    public EncryptionKeyDao(EncryptionKeyConverter converter, EncryptionKeyRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<EncryptionKey> findById(UUID externalId, DataType dataType) {
        EncryptionKeyPk pk = EncryptionKeyPk.builder()
            .externalId(uuidConverter.convertDomain(externalId))
            .dataType(dataType.name())
            .build();
        return findById(pk);
    }

    public List<EncryptionKey> getByUserId(UUID userId) {
        return converter.convertEntity(repository.getByUserId(uuidConverter.convertDomain(userId)));
    }
}