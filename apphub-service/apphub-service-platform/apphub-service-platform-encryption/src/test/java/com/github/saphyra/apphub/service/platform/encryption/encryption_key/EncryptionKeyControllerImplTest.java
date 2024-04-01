package com.github.saphyra.apphub.service.platform.encryption.encryption_key;

import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyCrService;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyDeletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EncryptionKeyControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_ID = UUID.randomUUID();

    @Mock
    private EncryptionKeyCrService encryptionKeyCrService;

    @Mock
    private EncryptionKeyDeletionService encryptionKeyDeletionService;

    @InjectMocks
    private EncryptionKeyControllerImpl underTest;

    @Mock
    private EncryptionKey encryptionKey;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getOrCreateEncryptionKey() {
        given(encryptionKeyCrService.createEncryptionKey(encryptionKey)).willReturn(encryptionKey);

        EncryptionKey result = underTest.getOrCreateEncryptionKey(encryptionKey, accessTokenHeader);

        assertThat(result).isEqualTo(encryptionKey);
    }

    @Test
    public void deleteEncryptionKey() {
        underTest.deleteEncryptionKey(DataType.TEST, EXTERNAL_ID, accessTokenHeader);

        verify(encryptionKeyDeletionService).deleteEncryptionKey(DataType.TEST, EXTERNAL_ID);
    }
}