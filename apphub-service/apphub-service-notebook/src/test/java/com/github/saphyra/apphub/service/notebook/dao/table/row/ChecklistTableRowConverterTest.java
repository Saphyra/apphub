package com.github.saphyra.apphub.service.notebook.dao.table.row;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
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
public class ChecklistTableRowConverterTest {
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final Integer ROW_INDEX = 456;
    private static final String ROW_ID_STRING = "row-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final String CHECKED = "checked";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private ChecklistTableRowConverter underTest;

    @BeforeEach
    public void setUp() {
        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
    }

    @Test
    public void convertDomain() {
        ChecklistTableRow domain = ChecklistTableRow.builder()
            .rowId(ROW_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .rowIndex(ROW_INDEX)
            .checked(true)
            .build();

        given(uuidConverter.convertDomain(ROW_ID)).willReturn(ROW_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(booleanEncryptor.encryptEntity(true, ACCESS_TOKEN_USER_ID)).willReturn(CHECKED);

        ChecklistTableRowEntity result = underTest.convertDomain(domain);

        assertThat(result.getRowId()).isEqualTo(ROW_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(result.getChecked()).isEqualTo(CHECKED);
    }

    @Test
    public void convertEntity() {
        ChecklistTableRowEntity entity = ChecklistTableRowEntity.builder()
            .rowId(ROW_ID_STRING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .rowIndex(ROW_INDEX)
            .checked(CHECKED)
            .build();

        given(uuidConverter.convertEntity(ROW_ID_STRING)).willReturn(ROW_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);
        given(booleanEncryptor.decryptEntity(CHECKED, ACCESS_TOKEN_USER_ID)).willReturn(true);

        ChecklistTableRow result = underTest.convertEntity(entity);

        assertThat(result.getRowId()).isEqualTo(ROW_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getRowIndex()).isEqualTo(ROW_INDEX);
        assertThat(result.isChecked()).isTrue();
    }
}