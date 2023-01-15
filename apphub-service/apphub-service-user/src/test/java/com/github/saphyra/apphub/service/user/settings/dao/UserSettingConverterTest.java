package com.github.saphyra.apphub.service.user.settings.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserSettingConverterTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CATEGORY = "category";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String USER_ID_STRING = "user-id";
    private static final String ENCRYPTED_VALUE = "encrypted-value";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private UserSettingConverter underTest;

    @BeforeEach
    public void setUp() {
        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
    }

    @Test
    public void convertDomain() {
        UserSetting domain = UserSetting.builder()
            .userId(USER_ID)
            .category(CATEGORY)
            .key(KEY)
            .value(VALUE)
            .build();

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encryptEntity(VALUE, ACCESS_TOKEN_USER_ID)).willReturn(ENCRYPTED_VALUE);

        UserSettingEntity result = underTest.convertDomain(domain);

        assertThat(result.getId().getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getId().getCategory()).isEqualTo(CATEGORY);
        assertThat(result.getId().getKey()).isEqualTo(KEY);
        assertThat(result.getValue()).isEqualTo(ENCRYPTED_VALUE);
    }

    @Test
    public void convertEntity() {
        UserSettingEntity entity = UserSettingEntity.builder()
            .id(
                UserSettingEntityId.builder()
                    .userId(USER_ID_STRING)
                    .category(CATEGORY)
                    .key(KEY)
                    .build()
            )
            .value(ENCRYPTED_VALUE)
            .build();

        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decryptEntity(ENCRYPTED_VALUE, ACCESS_TOKEN_USER_ID)).willReturn(VALUE);

        UserSetting result = underTest.convertEntity(entity);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getCategory()).isEqualTo(CATEGORY);
        assertThat(result.getKey()).isEqualTo(KEY);
        assertThat(result.getValue()).isEqualTo(VALUE);
    }
}