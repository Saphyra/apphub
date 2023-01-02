package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EncryptionKeyCreationServiceTest {
    private static final String ENCRYPTION_KEY = "encryption-key";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_ID = UUID.randomUUID();

    @Mock
    private EncryptionKeyRequestValidator encryptionKeyRequestValidator;

    @Mock
    private EncryptionKeyQueryService encryptionKeyQueryService;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private EncryptionKeyDao encryptionKeyDao;

    @InjectMocks
    private EncryptionKeyCreationService underTest;

    @Mock
    private EncryptionKey request;

    @After
    public void validate() {
        verify(encryptionKeyRequestValidator).validate(request);
    }

    @Test
    public void encryptionKeyAlreadyExists() {
        given(request.getExternalId()).willReturn(EXTERNAL_ID);
        given(request.getDataType()).willReturn(DataType.TEST);
        given(encryptionKeyQueryService.getEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT)).willReturn(Optional.of(ENCRYPTION_KEY));

        String result = underTest.createEncryptionKey(USER_ID, request, AccessMode.EDIT);

        assertThat(result).isEqualTo(ENCRYPTION_KEY);
    }

    @Test
    public void createEncryptionKey() {
        given(request.getExternalId()).willReturn(EXTERNAL_ID);
        given(request.getDataType()).willReturn(DataType.TEST);
        given(encryptionKeyQueryService.getEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT)).willReturn(Optional.empty());
        given(idGenerator.generateRandomId()).willReturn(ENCRYPTION_KEY);

        String result = underTest.createEncryptionKey(USER_ID, request, AccessMode.EDIT);

        assertThat(result).isEqualTo(ENCRYPTION_KEY);

        verify(request).setEncryptionKey(ENCRYPTION_KEY);
        verify(encryptionKeyDao).save(request);
    }
}