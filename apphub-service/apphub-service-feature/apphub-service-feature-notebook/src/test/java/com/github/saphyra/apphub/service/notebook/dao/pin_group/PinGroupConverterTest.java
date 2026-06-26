package com.github.saphyra.apphub.service.notebook.dao.pin_group;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static com.github.saphyra.apphub.service.notebook.dao.pin_group.PinGroupDaoConstants.COLUMN_PIN_GROUP_NAME;

@ExtendWith(MockitoExtension.class)
class PinGroupConverterTest {
    private static final UUID PIN_GROUP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String PIN_GROUP_NAME = "test-pin-group";
    private static final String PIN_GROUP_ID_STRING = "pin-group-id-string";
    private static final String USER_ID_STRING = "user-id-string";
    private static final String ENCRYPTED_PIN_GROUP_NAME = "encrypted-pin-group-name";
    private static final LocalDateTime LAST_OPENED = LocalDateTime.now();
    private static final Long EPOCH_SECOND = 1736939445L;
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";
    private static final String LIST_ITEM_IDS_JSON = "list-item-ids-json";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private PinGroupConverter underTest;

    @Test
    void convertDomain() {
        Set<UUID> listItemIds = new HashSet<>();
        listItemIds.add(LIST_ITEM_ID);

        PinGroup domain = PinGroup.builder()
            .userId(USER_ID)
            .pinGroupId(PIN_GROUP_ID)
            .pinGroupName(PIN_GROUP_NAME)
            .lastOpened(LAST_OPENED)
            .listItemIds(listItemIds)
            .build();

        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(uuidConverter.convertDomain(PIN_GROUP_ID)).willReturn(PIN_GROUP_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(stringEncryptor.encrypt(PIN_GROUP_NAME, ACCESS_TOKEN_USER_ID, PIN_GROUP_ID_STRING, COLUMN_PIN_GROUP_NAME)).willReturn(ENCRYPTED_PIN_GROUP_NAME);
        given(dateTimeUtil.toEpochSecond(LAST_OPENED)).willReturn(EPOCH_SECOND);
        given(objectMapper.writeValueAsString(listItemIds)).willReturn(LIST_ITEM_IDS_JSON);

        PinGroupEntity result = underTest.convertDomain(domain);

        assertThat(result)
            .returns(USER_ID_STRING, PinGroupEntity::getUserId)
            .returns(PIN_GROUP_ID_STRING, PinGroupEntity::getPinGroupId)
            .returns(ENCRYPTED_PIN_GROUP_NAME, PinGroupEntity::getPinGroupName)
            .returns(EPOCH_SECOND, PinGroupEntity::getLastOpened)
            .returns(LIST_ITEM_IDS_JSON, PinGroupEntity::getListItemIds);
    }

    @Test
    void convertEntity() {
        List<String> listItemIdStrings = List.of(LIST_ITEM_ID.toString());

        PinGroupEntity entity = PinGroupEntity.builder()
            .userId(USER_ID_STRING)
            .pinGroupId(PIN_GROUP_ID_STRING)
            .pinGroupName(ENCRYPTED_PIN_GROUP_NAME)
            .lastOpened(EPOCH_SECOND)
            .listItemIds(LIST_ITEM_IDS_JSON)
            .build();

        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(PIN_GROUP_ID_STRING)).willReturn(PIN_GROUP_ID);
        given(stringEncryptor.decrypt(ENCRYPTED_PIN_GROUP_NAME, ACCESS_TOKEN_USER_ID, PIN_GROUP_ID_STRING, COLUMN_PIN_GROUP_NAME)).willReturn(PIN_GROUP_NAME);
        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(dateTimeUtil.fromEpochSecond(EPOCH_SECOND)).willReturn(LAST_OPENED);
        doReturn(listItemIdStrings).when(objectMapper).readValue(anyString(), any(TypeReference.class));
        given(uuidConverter.convertEntity(any(List.class))).willReturn(List.of(LIST_ITEM_ID));

        PinGroup result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(USER_ID, PinGroup::getUserId)
            .returns(PIN_GROUP_ID, PinGroup::getPinGroupId)
            .returns(PIN_GROUP_NAME, PinGroup::getPinGroupName)
            .returns(LAST_OPENED, PinGroup::getLastOpened)
            .returns(new HashSet<>(Set.of(LIST_ITEM_ID)), PinGroup::getListItemIds);
    }
}