package com.github.saphyra.apphub.lib.encryption.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.saphyra.apphub.lib.common_util.Base64Encoder;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.encryption.base.DefaultEncryptor;
import com.github.saphyra.apphub.lib.encryption.base.EncryptedEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Slf4j
public class DefaultStringEncryptor implements StringEncryptor {
    private final Base64Encoder base64Encoder;
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public String encrypt(String entity, String key, String entityId, String column) {
        EncryptedEntity encryptedEntity = EncryptedEntity.builder()
            .entity(entity)
            .entityId(entityId)
            .column(column)
            .build();

        String stringified = objectMapperWrapper.writeValueAsString(encryptedEntity);

        DefaultEncryptor encryption = new DefaultEncryptor(base64Encoder, key);
        return encryption.encrypt(stringified);
    }

    @Override
    public String decrypt(String entity, String key, String entityId, String column) {
        if (isNull(entity)) {
            return null;
        }

        DefaultEncryptor decryption = new DefaultEncryptor(base64Encoder, key);

        String decrypted = decryption.decrypt(entity);

        return extractEntity(decrypted, entityId, column);
    }

    private String extractEntity(String decrypted, String entityId, String column) {
        try {
            EncryptedEntity encryptedEntity = objectMapperWrapper.getObjectMapper()
                .readValue(decrypted, EncryptedEntity.class);

            if (!Objects.equals(encryptedEntity.getEntityId(), entityId)) {
                throw new IllegalStateException("EntityId mismatch");
            }

            if (!Objects.equals(encryptedEntity.getColumn(), column)) {
                throw new IllegalStateException("Column mismatch");
            }

            return encryptedEntity.getEntity();
        } catch (JsonProcessingException e) {
            log.info("Deprecated encryption structure detected.");
            return decrypted;
        }
    }
}
