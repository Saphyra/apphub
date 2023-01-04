package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;

public class EncryptionKeyRequestValidatorTest {
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    private final EncryptionKeyRequestValidator underTest = new EncryptionKeyRequestValidator();

    @Test
    public void valid() {
        EncryptionKey encryptionKey = validEncryptionKey()
            .build();

        underTest.validate(encryptionKey);
    }

    @Test
    public void nullExternalId() {
        EncryptionKey encryptionKey = validEncryptionKey()
            .externalId(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(encryptionKey));

        ExceptionValidator.validateInvalidParam(ex, "externalId", "must not be null");
    }

    @Test
    public void nullDataType() {
        EncryptionKey encryptionKey = validEncryptionKey()
            .dataType(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(encryptionKey));

        ExceptionValidator.validateInvalidParam(ex, "dataType", "must not be null");
    }

    @Test
    public void nullUserId() {
        EncryptionKey encryptionKey = validEncryptionKey()
            .userId(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(encryptionKey));

        ExceptionValidator.validateInvalidParam(ex, "userId", "must not be null");
    }

    private EncryptionKey.EncryptionKeyBuilder validEncryptionKey() {
        return EncryptionKey.builder()
            .externalId(EXTERNAL_ID)
            .dataType(DataType.TEST)
            .userId(USER_ID);
    }
}