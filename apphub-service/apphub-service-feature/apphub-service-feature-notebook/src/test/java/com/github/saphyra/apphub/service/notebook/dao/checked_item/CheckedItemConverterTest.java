package com.github.saphyra.apphub.service.notebook.dao.checked_item;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.BooleanEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.checked_item.CheckedItemConverter.COLUMN_CHECKED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CheckedItemConverterTest {
    private static final UUID CHECKED_ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String CHECKED_ITEM_ID_STRING = "checked-item-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_CHECKED = "encrypted-checked";

    @Mock
    private BooleanEncryptor booleanEncryptor;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private CheckedItemConverter underTest;

    @Test
    void convertDomain() {
        CheckedItem domain = CheckedItem.builder()
            .checkedItemId(CHECKED_ITEM_ID)
            .userId(USER_ID)
            .checked(true)
            .build();
        given(uuidConverter.convertDomain(CHECKED_ITEM_ID)).willReturn(CHECKED_ITEM_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(booleanEncryptor.encrypt(true, USER_ID_FROM_ACCESS_TOKEN, CHECKED_ITEM_ID_STRING, COLUMN_CHECKED)).willReturn(ENCRYPTED_CHECKED);

        CheckedItemEntity result = underTest.convertDomain(domain);

        assertThat(result.getCheckedItemId()).isEqualTo(CHECKED_ITEM_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getChecked()).isEqualTo(ENCRYPTED_CHECKED);
    }

    @Test
    void convertEntity() {
        CheckedItemEntity domain = CheckedItemEntity.builder()
            .checkedItemId(CHECKED_ITEM_ID_STRING)
            .userId(USER_ID_STRING)
            .checked(ENCRYPTED_CHECKED)
            .build();
        given(uuidConverter.convertEntity(CHECKED_ITEM_ID_STRING)).willReturn(CHECKED_ITEM_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(booleanEncryptor.decrypt(ENCRYPTED_CHECKED, USER_ID_FROM_ACCESS_TOKEN, CHECKED_ITEM_ID_STRING, COLUMN_CHECKED)).willReturn(true);

        CheckedItem result = underTest.convertEntity(domain);

        assertThat(result.getCheckedItemId()).isEqualTo(CHECKED_ITEM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getChecked()).isTrue();
    }
}