package com.github.saphyra.apphub.lib.encryption_dao;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class EncryptionProxy {
    public void deleteEncryptionKey(UUID externalId, DataType dataType) {
        //TODO implement
    }

    public EncryptionKey getEncryptionKey(UUID externalId, DataType dataType) {
        return  null;//TODO implement
    }
}
