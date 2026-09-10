package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.encryption.impl.StringEncryptor;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.github.saphyra.apphub.service.feature.calendar.common.dao.CalendarDaoConstants.COLUMN_LABEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeprecatedLabelConverterTest {
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL_ID_STRING = "label-id";
    private static final String ACCESS_TOKEN_USER_ID = "access-token-user-id";
    private static final String LABEL = "label";
    private static final String LABEL_ENCRYPTED = "label-encrypted";
    private static final @NonNull UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private StringEncryptor stringEncryptor;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private DeprecatedLabelConverter underTest;

    @Test
    void convertDomain() {
        Label domain = Label.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(stringEncryptor.encrypt(LABEL, ACCESS_TOKEN_USER_ID, LABEL_ID_STRING, COLUMN_LABEL)).willReturn(LABEL_ENCRYPTED);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        LabelEntity result = underTest.convertDomain(domain);

        assertThat(result)
            .returns(USER_ID_STRING, LabelEntity::getUserId)
            .returns(LABEL_ID_STRING, LabelEntity::getLabelId)
            .returns(LABEL_ENCRYPTED, LabelEntity::getLabel);
    }

    @Test
    void convertEntity() {
        LabelEntity entity = LabelEntity.builder()
            .userId(USER_ID_STRING)
            .labelId(LABEL_ID_STRING)
            .label(LABEL_ENCRYPTED)
            .build();

        given(uuidConverter.convertEntity(LABEL_ID_STRING)).willReturn(LABEL_ID);
        given(accessTokenProvider.getUserIdAsString()).willReturn(ACCESS_TOKEN_USER_ID);
        given(stringEncryptor.decrypt(LABEL_ENCRYPTED, ACCESS_TOKEN_USER_ID, LABEL_ID_STRING, COLUMN_LABEL)).willReturn(LABEL);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);

        Label result = underTest.convertEntity(entity);

        assertThat(result)
            .returns(USER_ID, Label::getUserId)
            .returns(LABEL_ID, Label::getLabelId)
            .returns(LABEL, Label::getLabel);
    }
}

