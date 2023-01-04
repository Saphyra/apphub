package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.service.SharedDataAccessService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class EncryptionKeyDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_ID = UUID.randomUUID();

    @Mock
    private EncryptionKeyDao encryptionKeyDao;

    @Mock
    private SharedDataAccessService sharedDataAccessService;

    @Mock
    private SharedDataDao sharedDataDao;

    @InjectMocks
    private EncryptionKeyDeletionService underTest;

    @Mock
    private EncryptionKey encryptionKey;

    @Test
    public void deleteEncryptionKey_unsupportedAccessMode() {
        Throwable ex = catchThrowable(() -> underTest.deleteEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.READ));

        ExceptionValidator.validateInvalidParam(ex, "accessMode", String.format("must be one of %s", List.of(AccessMode.EDIT, AccessMode.DELETE)));
    }

    @Test
    public void deleteEncryptionKey_keyNotFound() {
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.empty());

        underTest.deleteEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT);

        verify(encryptionKeyDao, times(0)).delete(any());
    }

    @Test
    public void deleteEncryptionKey_noAccess() {
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.of(encryptionKey));
        given(sharedDataAccessService.hasAccess(USER_ID, AccessMode.EDIT, EXTERNAL_ID, DataType.TEST)).willReturn(false);
        given(encryptionKey.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.deleteEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void deleteEncryptionKey_hasAccess() {
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.of(encryptionKey));
        given(sharedDataAccessService.hasAccess(USER_ID, AccessMode.EDIT, EXTERNAL_ID, DataType.TEST)).willReturn(true);
        given(encryptionKey.getUserId()).willReturn(UUID.randomUUID());

        underTest.deleteEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT);

        verify(encryptionKeyDao).delete(encryptionKey);
        verify(sharedDataDao).deleteByExternalIdAndDataType(EXTERNAL_ID, DataType.TEST);
    }

    @Test
    public void deleteEncryptionKey_ownRecord() {
        given(encryptionKeyDao.findById(EXTERNAL_ID, DataType.TEST)).willReturn(Optional.of(encryptionKey));
        given(encryptionKey.getUserId()).willReturn(USER_ID);

        underTest.deleteEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT);

        verifyNoInteractions(sharedDataAccessService);
        verify(encryptionKeyDao).delete(encryptionKey);
        verify(sharedDataDao).deleteByExternalIdAndDataType(EXTERNAL_ID, DataType.TEST);
    }

    @Test
    public void deleteByUserId() {
        given(encryptionKeyDao.getByUserId(USER_ID)).willReturn(List.of(encryptionKey));
        given(encryptionKey.getExternalId()).willReturn(EXTERNAL_ID);
        given(encryptionKey.getDataType()).willReturn(DataType.TEST);

        underTest.deleteByUserId(USER_ID);

        verify(sharedDataDao).deleteByExternalIdAndDataType(EXTERNAL_ID, DataType.TEST);
        verify(encryptionKeyDao).delete(encryptionKey);
    }
}