package com.github.saphyra.apphub.service.platform.encryption.encryption_key.service;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.dao.EncryptionKeyDao;
import com.github.saphyra.apphub.service.platform.encryption.shared_data.dao.SharedDataDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EncryptionKeyDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_ID = UUID.randomUUID();

    @Mock
    private EncryptionKeyDao encryptionKeyDao;

    @Mock
    private SharedDataDao sharedDataDao;

    @InjectMocks
    private EncryptionKeyDeletionService underTest;

    @Mock
    private EncryptionKey encryptionKey;

    @Test
    public void deleteEncryptionKey() {
        underTest.deleteEncryptionKey(DataType.TEST, EXTERNAL_ID);

        verify(encryptionKeyDao).delete(EXTERNAL_ID, DataType.TEST);
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