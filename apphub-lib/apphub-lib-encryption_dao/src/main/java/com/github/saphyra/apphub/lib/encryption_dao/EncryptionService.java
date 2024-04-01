package com.github.saphyra.apphub.lib.encryption_dao;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.base.AbstractEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.LongEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EncryptionService {
    private final StringEncryptor stringEncryptor;
    private final IntegerEncryptor integerEncryptor;
    private final LongEncryptor longEncryptor;
    private final BooleanEncryptor booleanEncryptor;
    private final EncryptionKeyCache encryptionKeyCache;
    private final UuidConverter uuidConverter;

    public String encrypt(String value, String column, UUID externalId, DataType dataType, Predicate<EncryptionKey> accessChecker) {
        return encrypt(value, column, externalId, dataType, accessChecker, stringEncryptor);
    }

    public String decryptString(String value, String column, UUID externalId, DataType dataType, Predicate<EncryptionKey> accessChecker) {
        return decrypt(value, column, externalId, dataType, accessChecker, stringEncryptor);
    }

    public String encrypt(Boolean value, String column, UUID externalId, DataType dataType, Predicate<EncryptionKey> accessChecker) {
        return encrypt(value, column, externalId, dataType, accessChecker, booleanEncryptor);
    }

    public Boolean decryptBoolean(String value, String column, UUID externalId, DataType dataType, Predicate<EncryptionKey> accessChecker) {
        return decrypt(value, column, externalId, dataType, accessChecker, booleanEncryptor);
    }

    private <T> String encrypt(T value, String column, UUID externalId, DataType dataType, Predicate<EncryptionKey> accessChecker, AbstractEncryptor<T> encryptor) {
        EncryptionKey encryptionKey = encryptionKeyCache.getValidated(new BiWrapper<>(externalId, dataType));

        if (!accessChecker.test(encryptionKey)) {
            throw ExceptionFactory.forbiddenOperation("Resource access check failed.");
        }

        return encryptor.encrypt(value, encryptionKey.getEncryptionKey(), uuidConverter.convertDomain(externalId), column);
    }

    private <T> T decrypt(String value, String column, UUID externalId, DataType dataType, Predicate<EncryptionKey> accessChecker, AbstractEncryptor<T> encryptor) {
        EncryptionKey encryptionKey = encryptionKeyCache.getValidated(new BiWrapper<>(externalId, dataType));

        if (!accessChecker.test(encryptionKey)) {
            throw ExceptionFactory.forbiddenOperation("Resource access check failed.");
        }

        return encryptor.decrypt(value, encryptionKey.getEncryptionKey(), uuidConverter.convertDomain(externalId), column);
    }
}
