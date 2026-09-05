package com.github.saphyra.apphub.service.feature.calendar.domain.label.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL = "label";
    private static final String USER_ID_STRING = "user-id";
    private static final String LABEL_ID_STRING = "label-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private LabelConverter underTest;

    @Test
    void convertDomain() {
        Label domain = Label.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(USER_ID_STRING, LabelEntity::getUserId)
            .returns(LABEL_ID_STRING, LabelEntity::getLabelId)
            .returns(LABEL, LabelEntity::getLabel);
    }

    @Test
    void convertEntity() {
        LabelEntity entity = LabelEntity.builder()
            .userId(USER_ID_STRING)
            .labelId(LABEL_ID_STRING)
            .label(LABEL)
            .build();

        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(LABEL_ID_STRING)).willReturn(LABEL_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(USER_ID, Label::getUserId)
            .returns(LABEL_ID, Label::getLabelId)
            .returns(LABEL, Label::getLabel);
    }
}