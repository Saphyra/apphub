package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EncryptionKeyCrServiceTest {
    private static final String ENCRYPTION_KEY = "encryption-key";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_ID = UUID.randomUUID();

    @Mock
    private EncryptionKeyRequestValidator encryptionKeyRequestValidator;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private EncryptionKeyDao encryptionKeyDao;

    @InjectMocks
    private EncryptionKeyCrService underTest;

    @Mock
    private EncryptionKey request;

    @Mock
    private EncryptionKey encryptionKey;

    @AfterEach
    public void validate() {
        verify(encryptionKeyRequestValidator).validate(request);
    }

    @Test
    public void encryptionKeyAlreadyExists() {
        given(request.getExternalId()).willReturn(EXTERNAL_ID);
        given(request.getDataType()).willReturn(DataType.TEST);
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.of(encryptionKey));

        EncryptionKey result = underTest.createEncryptionKey(request);

        assertThat(result).isEqualTo(encryptionKey);
    }

    @Test
    public void createEncryptionKey() {
        given(request.getExternalId()).willReturn(EXTERNAL_ID);
        given(request.getDataType()).willReturn(DataType.TEST);
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.empty());
        given(idGenerator.generateRandomId()).willReturn(ENCRYPTION_KEY);

        EncryptionKey result = underTest.createEncryptionKey(request);

        assertThat(result).isEqualTo(request);

        verify(request).setEncryptionKey(ENCRYPTION_KEY);
        verify(encryptionKeyDao).save(request);
    }
}