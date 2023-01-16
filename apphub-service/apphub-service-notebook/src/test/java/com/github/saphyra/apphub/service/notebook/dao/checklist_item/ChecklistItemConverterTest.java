package com.github.saphyra.apphub.service.notebook.dao.checklist_item;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.encryption.impl.IntegerEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ChecklistItemConverterTest {
    private static final UUID CHECKLIST_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID PARENT = UUID.randomUUID();
    private static final Integer DECRYPTED_ORDER = 345;
    private static final Boolean DECRYPTED_CHECKED = true;
    private static final UUID USER_ID_IN_ACCESS_TOKEN_HEADER = UUID.randomUUID();
    private static final String USER_ID_IN_ACCESS_TOKEN_HEADER_STRING = "user-id-in-access-token-header";
    private static final String CHECKLIST_ITEM_ID_STRING = "checklist-item-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String PARENT_STRING = "parent";
    private static final String ENCRYPTED_ORDER = "encrypted-order";
    private static final String ENCRYPTED_CHECKED = "encrypted-checked";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private IntegerEncryptor integerEncryptor;

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @InjectMocks
    private ChecklistItemConverter underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Test
    public void convertEntity() {
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID_IN_ACCESS_TOKEN_HEADER);
        given(uuidConverter.convertDomain(USER_ID_IN_ACCESS_TOKEN_HEADER)).willReturn(USER_ID_IN_ACCESS_TOKEN_HEADER_STRING);

        given(uuidConverter.convertEntity(CHECKLIST_ITEM_ID_STRING)).willReturn(CHECKLIST_ITEM_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PARENT_STRING)).willReturn(PARENT);
        given(integerEncryptor.decryptEntity(ENCRYPTED_ORDER, USER_ID_IN_ACCESS_TOKEN_HEADER_STRING)).willReturn(DECRYPTED_ORDER);
        given(booleanEncryptor.decryptEntity(ENCRYPTED_CHECKED, USER_ID_IN_ACCESS_TOKEN_HEADER_STRING)).willReturn(DECRYPTED_CHECKED);

        ChecklistItemEntity entity = ChecklistItemEntity.builder()
            .checklistItemId(CHECKLIST_ITEM_ID_STRING)
            .userId(USER_ID_STRING)
            .parent(PARENT_STRING)
            .order(ENCRYPTED_ORDER)
            .checked(ENCRYPTED_CHECKED)
            .build();

        ChecklistItem result = underTest.convertEntity(entity);

        assertThat(result.getChecklistItemId()).isEqualTo(CHECKLIST_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getOrder()).isEqualTo(DECRYPTED_ORDER);
        assertThat(result.getChecked()).isEqualTo(DECRYPTED_CHECKED);
    }

    @Test
    public void convertDomain() {
        given(accessTokenProvider.get()).willReturn(accessTokenHeader);
        given(accessTokenHeader.getUserId()).willReturn(USER_ID_IN_ACCESS_TOKEN_HEADER);
        given(uuidConverter.convertDomain(USER_ID_IN_ACCESS_TOKEN_HEADER)).willReturn(USER_ID_IN_ACCESS_TOKEN_HEADER_STRING);
        given(uuidConverter.convertDomain(CHECKLIST_ITEM_ID)).willReturn(CHECKLIST_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(PARENT)).willReturn(PARENT_STRING);
        given(integerEncryptor.encryptEntity(DECRYPTED_ORDER, USER_ID_IN_ACCESS_TOKEN_HEADER_STRING)).willReturn(ENCRYPTED_ORDER);
        given(booleanEncryptor.encryptEntity(DECRYPTED_CHECKED, USER_ID_IN_ACCESS_TOKEN_HEADER_STRING)).willReturn(ENCRYPTED_CHECKED);

        ChecklistItem checklistItem = ChecklistItem.builder()
            .checklistItemId(CHECKLIST_ITEM_ID)
            .userId(USER_ID)
            .parent(PARENT)
            .order(DECRYPTED_ORDER)
            .checked(DECRYPTED_CHECKED)
            .build();

        ChecklistItemEntity result = underTest.convertDomain(checklistItem);

        assertThat(result.getChecklistItemId()).isEqualTo(CHECKLIST_ITEM_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getParent()).isEqualTo(PARENT_STRING);
        assertThat(result.getOrder()).isEqualTo(ENCRYPTED_ORDER);
        assertThat(result.getChecked()).isEqualTo(ENCRYPTED_CHECKED);
    }
}