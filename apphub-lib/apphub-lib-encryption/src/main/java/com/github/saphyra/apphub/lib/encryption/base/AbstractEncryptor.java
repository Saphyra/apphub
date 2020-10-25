package com.github.saphyra.apphub.lib.encryption.base;

public abstract class AbstractEncryptor<T> implements Encryptor<T> {
    @Override
    public String encryptEntity(T entity, String key) {
        validateKey(key);
        if (entity == null) {
            return null;
        }
        return encrypt(entity, key);
    }

    @Override
    public T decryptEntity(String entity, String key) {
        validateKey(key);
        if (entity == null) {
            return null;
        }
        return decrypt(entity, key);
    }

    private void validateKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key must not be null.");
        }
    }

    protected abstract String encrypt(T entity, String key);

    protected abstract T decrypt(String entity, String key);
}
