package com.github.saphyra.apphub.service.calendar.domain.label.dao;

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

@ExtendWith(MockitoExtension.class)
class LabelConverterTest {
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LABEL = "label";
    private static final String LABEL_ID_STRING = "label-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String USER_ID_FROM_ACCESS_TOKEN = "user-id-from-access-token";
    private static final String ENCRYPTED_LABEL = "encrypted-label";

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @InjectMocks
    private LabelConverter underTest;

    @Test
    void convertDomain() {
        Label domain = Label.builder()
            .labelId(LABEL_ID)
            .userId(USER_ID)
            .label(LABEL)
            .build();

        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.encrypt(LABEL, USER_ID_FROM_ACCESS_TOKEN, LABEL_ID_STRING, LabelConverter.COLUMN_LABEL)).willReturn(ENCRYPTED_LABEL);

        assertThat(underTest.convertDomain(domain))
            .returns(LABEL_ID_STRING, LabelEntity::getLabelId)
            .returns(USER_ID_STRING, LabelEntity::getUserId)
            .returns(ENCRYPTED_LABEL, LabelEntity::getLabel);
    }

    @Test
    void convertEntity() {
        LabelEntity entity = LabelEntity.builder()
            .labelId(LABEL_ID_STRING)
            .userId(USER_ID_STRING)
            .label(ENCRYPTED_LABEL)
            .build();

        given(uuidConverter.convertEntity(LABEL_ID_STRING)).willReturn(LABEL_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(USER_ID_FROM_ACCESS_TOKEN);
        given(stringEncryptor.decrypt(ENCRYPTED_LABEL, USER_ID_FROM_ACCESS_TOKEN, LABEL_ID_STRING, LabelConverter.COLUMN_LABEL)).willReturn(LABEL);

        assertThat(underTest.convertEntity(entity))
            .returns(LABEL_ID, Label::getLabelId)
            .returns(USER_ID, Label::getUserId)
            .returns(LABEL, Label::getLabel);
    }
}