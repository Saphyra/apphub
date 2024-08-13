package com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.custom.villany_atesz.toolbox.dao.tool_type.ToolTypeConverter.COLUMN_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ToolTypeConverterTest {
    private static final UUID TOOL_TYPE_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NAME = "name";
    private static final String TOOL_TYPE_ID_STRING = "tool-type-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_NAME = "encrypted-name";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private ToolTypeConverter underTest;

    @Test
    void convertDomain() {
        ToolType domain = ToolType.builder()
            .toolTypeId(TOOL_TYPE_ID)
            .userId(USER_ID)
            .name(NAME)
            .build();

        given(uuidConverter.convertDomain(TOOL_TYPE_ID)).willReturn(TOOL_TYPE_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);

        given(stringEncryptor.encrypt(NAME, USER_ID_FROM_ACCESS_TOKEN, TOOL_TYPE_ID_STRING, COLUMN_NAME)).willReturn(ENCRYPTED_NAME);

        assertThat(underTest.convertDomain(domain))
            .returns(TOOL_TYPE_ID_STRING, ToolTypeEntity::getToolTypeId)
            .returns(USER_ID_STRING, ToolTypeEntity::getUserId)
            .returns(ENCRYPTED_NAME, ToolTypeEntity::getName);
    }

    @Test
    void convertEntity() {
        ToolTypeEntity entity = ToolTypeEntity.builder()
            .toolTypeId(TOOL_TYPE_ID_STRING)
            .userId(USER_ID_STRING)
            .name(ENCRYPTED_NAME)
            .build();

        given(uuidConverter.convertEntity(TOOL_TYPE_ID_STRING)).willReturn(TOOL_TYPE_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);

        given(stringEncryptor.decrypt(ENCRYPTED_NAME, USER_ID_FROM_ACCESS_TOKEN, TOOL_TYPE_ID_STRING, COLUMN_NAME)).willReturn(NAME);

        assertThat(underTest.convertEntity(entity))
            .returns(TOOL_TYPE_ID, ToolType::getToolTypeId)
            .returns(USER_ID, ToolType::getUserId)
            .returns(NAME, ToolType::getName);
    }
}