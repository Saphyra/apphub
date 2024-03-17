package com.github.saphyra.apphub.service.notebook.dao.pin.group;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupConverter.COLUMN_LAST_OPENED;
import static com.github.saphyra.apphub.service.notebook.dao.pin.group.PinGroupConverter.COLUMN_PIN_GROUP_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PinGroupConverterTest {
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PIN_GROUP_NAME = "pin-group-name";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";
    private static final String PIN_GROUP_ID_STRING = "pin-group-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String ENCRYPTED_PIN_GROUP_NAME = "encrypted-pin-group-name";
    private static final LocalDateTime LAST_OPENED = LocalDateTime.now();
    private static final String ENCRYPTED_LAST_OPENED = "last-opened";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private PinGroupConverter underTest;

    @Test
    void convertDomain() {
        PinGroup pinGroup = PinGroup.builder()
            .pinGroupId(PIN_GROUP_ID)
            .userId(USER_ID)
            .pinGroupName(PIN_GROUP_NAME)
            .lastOpened(LAST_OPENED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(PIN_GROUP_NAME, ACCESS_TOKEN_USER_ID, PIN_GROUP_ID_STRING, COLUMN_PIN_GROUP_NAME)).willReturn(ENCRYPTED_PIN_GROUP_NAME);
        given(stringEncryptor.encrypt(LAST_OPENED.toString(), ACCESS_TOKEN_USER_ID, PIN_GROUP_ID_STRING, COLUMN_LAST_OPENED)).willReturn(ENCRYPTED_LAST_OPENED);

        PinGroupEntity result = underTest.convertDomain(pinGroup);

        assertThat(result)
            .returns(PIN_GROUP_ID_STRING, PinGroupEntity::getPinGroupId)
            .returns(USER_ID_STRING, PinGroupEntity::getUserId)
            .returns(ENCRYPTED_PIN_GROUP_NAME, PinGroupEntity::getPinGroupName)
            .returns(ENCRYPTED_LAST_OPENED, PinGroupEntity::getLastOpened);
    }

    @Test
    void convertEntity() {
        PinGroupEntity pinGroupEntity = PinGroupEntity.builder()
            .pinGroupId(PIN_GROUP_ID_STRING)
            .userId(USER_ID_STRING)
            .pinGroupName(ENCRYPTED_PIN_GROUP_NAME)
            .lastOpened(ENCRYPTED_LAST_OPENED)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertEntity(PIN_GROUP_ID_STRING)).willReturn(PIN_GROUP_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_PIN_GROUP_NAME, ACCESS_TOKEN_USER_ID, PIN_GROUP_ID_STRING, COLUMN_PIN_GROUP_NAME)).willReturn(PIN_GROUP_NAME);
        given(stringEncryptor.decrypt(ENCRYPTED_LAST_OPENED, ACCESS_TOKEN_USER_ID, PIN_GROUP_ID_STRING, COLUMN_LAST_OPENED)).willReturn(LAST_OPENED.toString());

        PinGroup result = underTest.convertEntity(pinGroupEntity);

        assertThat(result)
            .returns(PIN_GROUP_ID, PinGroup::getPinGroupId)
            .returns(USER_ID, PinGroup::getUserId)
            .returns(PIN_GROUP_NAME, PinGroup::getPinGroupName)
            .returns(LAST_OPENED, PinGroup::getLastOpened);
    }

    @Test
    void convertEntity_nullLastOpened() {
        PinGroupEntity pinGroupEntity = PinGroupEntity.builder()
            .pinGroupId(PIN_GROUP_ID_STRING)
            .userId(USER_ID_STRING)
            .pinGroupName(ENCRYPTED_PIN_GROUP_NAME)
            .lastOpened(null)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertEntity(PIN_GROUP_ID_STRING)).willReturn(PIN_GROUP_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_PIN_GROUP_NAME, ACCESS_TOKEN_USER_ID, PIN_GROUP_ID_STRING, COLUMN_PIN_GROUP_NAME)).willReturn(PIN_GROUP_NAME);

        PinGroup result = underTest.convertEntity(pinGroupEntity);

        assertThat(result)
            .returns(PIN_GROUP_ID, PinGroup::getPinGroupId)
            .returns(USER_ID, PinGroup::getUserId)
            .returns(PIN_GROUP_NAME, PinGroup::getPinGroupName)
            .returns(LocalDateTime.MIN, PinGroup::getLastOpened);
    }
}