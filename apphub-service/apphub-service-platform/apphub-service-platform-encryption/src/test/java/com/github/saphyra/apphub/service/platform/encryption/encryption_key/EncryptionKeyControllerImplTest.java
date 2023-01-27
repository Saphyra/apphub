package com.github.saphyra.apphub.service.platform.encryption.encryption_key;

import com.github.saphyra.apphub.api.platform.encryption.model.AccessMode;
import com.github.saphyra.apphub.api.platform.encryption.model.DataType;
import com.github.saphyra.apphub.api.platform.encryption.model.EncryptionKey;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyCreationService;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyDeletionService;
import com.github.saphyra.apphub.service.platform.encryption.encryption_key.service.EncryptionKeyQueryService;
import org.junit.jupiter.api.BeforeEach;
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
public class EncryptionKeyControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EXTERNAL_ID = UUID.randomUUID();
    private static final String ENCRYPTION_KEY = "encryption-key";

    @Mock
    private EncryptionKeyQueryService encryptionKeyQueryService;

    @Mock
    private EncryptionKeyCreationService encryptionKeyCreationService;

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
    public void createEncryptionKey() {
        given(encryptionKeyCreationService.createEncryptionKey(USER_ID, encryptionKey, AccessMode.EDIT)).willReturn(ENCRYPTION_KEY);

        String result = underTest.createEncryptionKey(encryptionKey, AccessMode.EDIT, accessTokenHeader);

        assertThat(result).isEqualTo(ENCRYPTION_KEY);
    }

    @Test
    public void deleteEncryptionKey() {
        underTest.deleteEncryptionKey(DataType.TEST, EXTERNAL_ID, AccessMode.EDIT, accessTokenHeader);

        verify(encryptionKeyDeletionService).deleteEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT);
    }

    @Test
    public void getEncryptionKey() {
        given(encryptionKeyQueryService.getEncryptionKey(USER_ID, DataType.TEST, EXTERNAL_ID, AccessMode.EDIT)).willReturn(Optional.of(ENCRYPTION_KEY));

        String result = underTest.getEncryptionKey(DataType.TEST, EXTERNAL_ID, AccessMode.EDIT, accessTokenHeader);

        assertThat(result).isEqualTo(ENCRYPTION_KEY);
    }
}