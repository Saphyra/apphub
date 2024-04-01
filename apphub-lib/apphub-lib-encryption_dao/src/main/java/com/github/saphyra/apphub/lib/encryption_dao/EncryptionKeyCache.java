package com.github.saphyra.apphub.lib.encryption_dao;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.AbstractCache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
class EncryptionKeyCache extends AbstractCache<BiWrapper<UUID, DataType>, EncryptionKey> {
    private final EncryptionProxy encryptionProxy;

    public EncryptionKeyCache(EncryptionProxy encryptionProxy) {
        super(CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).expireAfterWrite(5, TimeUnit.MINUTES).build());
        this.encryptionProxy = encryptionProxy;
    }

    @Override
    protected Optional<EncryptionKey> load(BiWrapper<UUID, DataType> key) {
        return Optional.of(encryptionProxy.getEncryptionKey(key.getEntity1(), key.getEntity2()));
    }
}
