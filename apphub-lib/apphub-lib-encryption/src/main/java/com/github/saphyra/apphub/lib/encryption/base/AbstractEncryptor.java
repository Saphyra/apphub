package com.github.saphyra.apphub.lib.encryption.base;

public interface AbstractEncryptor<T> {
    String encrypt(T entity, String key, String entityId, String column);

    T decrypt(String entity, String key, String entityId, String column);
}
