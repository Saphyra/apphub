package com.github.saphyra.apphub.service.notebook.dao.column_type;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
class ColumnTypeConverterTest {
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String COLUMN_ID_STRING = "column-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_TYPE = "encrypted-type";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private ColumnTypeConverter underTest;

    @Test
    void convertDomain() {
        ColumnTypeDto domain = ColumnTypeDto.builder()
            .columnId(COLUMN_ID)
            .userId(USER_ID)
            .type(ColumnType.LINK)
            .build();

        given(uuidConverter.convertDomain(COLUMN_ID)).willReturn(COLUMN_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.encryptEntity(ColumnType.LINK.name(), USER_ID_FROM_ACCESS_TOKEN)).willReturn(ENCRYPTED_TYPE);

        ColumnTypeEntity result = underTest.convertDomain(domain);

        assertThat(result.getColumnId()).isEqualTo(COLUMN_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getType()).isEqualTo(ENCRYPTED_TYPE);
    }

    @Test
    void convertEntity() {
        ColumnTypeEntity entity = ColumnTypeEntity.builder()
            .columnId(COLUMN_ID_STRING)
            .userId(USER_ID_STRING)
            .type(ENCRYPTED_TYPE)
            .build();

        given(uuidConverter.convertEntity(COLUMN_ID_STRING)).willReturn(COLUMN_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.decryptEntity(ENCRYPTED_TYPE, USER_ID_FROM_ACCESS_TOKEN)).willReturn(ColumnType.LINK.name());

        ColumnTypeDto result = underTest.convertEntity(entity);

        assertThat(result.getColumnId()).isEqualTo(COLUMN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getType()).isEqualTo(ColumnType.LINK);
    }
}