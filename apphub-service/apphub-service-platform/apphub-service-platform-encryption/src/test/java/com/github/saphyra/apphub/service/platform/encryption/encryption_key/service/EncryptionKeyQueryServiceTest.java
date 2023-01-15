package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.service.SharedDataAccessService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class EncryptionKeyQueryServiceTest {
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ENCRYPTION_KEY = "encryption-key";

    @Mock
    private EncryptionKeyDao encryptionKeyDao;

    @Mock
    private SharedDataAccessService sharedDataAccessService;

    @InjectMocks
    private EncryptionKeyQueryService underTest;

    @Mock
    private EncryptionKey encryptionKey;

    @Test
    public void encryptionKeyNotFound() {
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.empty());

        Optional<String> result = underTest.getEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT);

        assertThat(result).isEmpty();
    }

    @Test
    public void noAccess() {
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.of(encryptionKey));
        given(encryptionKey.getUserId()).willReturn(UUID.randomUUID());
        given(sharedDataAccessService.hasAccess(USER_ID, AccessMode.EDIT, EXTERNAL_ID, DataType.TEST)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.getEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void hasAccess() {
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.of(encryptionKey));
        given(encryptionKey.getUserId()).willReturn(UUID.randomUUID());
        given(sharedDataAccessService.hasAccess(USER_ID, AccessMode.EDIT, EXTERNAL_ID, DataType.TEST)).willReturn(true);
        given(encryptionKey.getEncryptionKey()).willReturn(ENCRYPTION_KEY);

        Optional<String> result = underTest.getEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT);

        assertThat(result).contains(ENCRYPTION_KEY);
    }

    @Test
    public void ownRecord() {
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.of(encryptionKey));
        given(encryptionKey.getUserId()).willReturn(USER_ID);
        given(encryptionKey.getEncryptionKey()).willReturn(ENCRYPTION_KEY);

        Optional<String> result = underTest.getEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT);

        assertThat(result).contains(ENCRYPTION_KEY);

        verifyNoInteractions(sharedDataAccessService);
    }
}