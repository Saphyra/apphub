package com.github.saphyra.apphub.lib.encryption.base;

public interface Encryptor <T> {
    String encryptEntity(T entity, String key);
    T decryptEntity(String entity, String key);
}
