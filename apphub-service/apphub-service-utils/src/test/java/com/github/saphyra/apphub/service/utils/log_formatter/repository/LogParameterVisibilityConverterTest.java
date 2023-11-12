package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.utils.log_formatter.repository.LogParameterVisibilityConverter.COLUMN_PARAMETER;
import static com.github.saphyra.apphub.service.utils.log_formatter.repository.LogParameterVisibilityConverter.COLUMN_VISIBLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LogParameterVisibilityConverterTest {
    private static final String ID_STRING = "id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ENCRYPTED_PARAMETER = "encrypted-parameter";
    private static final String ENCRYPTED_VISIBLE = "encrypted-visible";
    private static final UUID ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String DECRYPTED_PARAMETER = "decrypted-parameter";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private LogParameterVisibilityConverter underTest;

    @BeforeEach
    public void setUp() {
        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
    }

    @Test
    public void convertEntity() {
        LogParameterVisibilityEntity entity = LogParameterVisibilityEntity.builder()
            .id(ID_STRING)
            .userId(USER_ID_STRING)
            .parameter(ENCRYPTED_PARAMETER)
            .visible(ENCRYPTED_VISIBLE)
            .build();

        given(uuidConverter.convertEntity(ID_STRING)).willReturn(ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_PARAMETER, ACCESS_TOKEN_USER_ID, ID_STRING, COLUMN_PARAMETER)).willReturn(DECRYPTED_PARAMETER);
        given(booleanEncryptor.decrypt(ENCRYPTED_VISIBLE, ACCESS_TOKEN_USER_ID, ID_STRING, COLUMN_VISIBLE)).willReturn(true);

        LogParameterVisibility result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParameter()).isEqualTo(DECRYPTED_PARAMETER);
        assertThat(result.isVisible()).isTrue();
    }

    @Test
    public void convertDomain() {
        LogParameterVisibility domain = LogParameterVisibility.builder()
            .id(ID)
            .userId(USER_ID)
            .parameter(DECRYPTED_PARAMETER)
            .visible(true)
            .build();

        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(DECRYPTED_PARAMETER, ACCESS_TOKEN_USER_ID, ID_STRING, COLUMN_PARAMETER)).willReturn(ENCRYPTED_PARAMETER);
        given(booleanEncryptor.encrypt(true, ACCESS_TOKEN_USER_ID, ID_STRING, COLUMN_VISIBLE)).willReturn(ENCRYPTED_VISIBLE);

        LogParameterVisibilityEntity result = underTest.convertDomain(domain);

        assertThat(result.getId()).isEqualTo(ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParameter()).isEqualTo(ENCRYPTED_PARAMETER);
        assertThat(result.getVisible()).isEqualTo(ENCRYPTED_VISIBLE);
    }
}