package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.api.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.Label;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LabelMapperTest {
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL = "label";

    @InjectMocks
    private LabelMapper underTest;

    @Test
    void toResponse() {
        Label label = Label.builder()
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        assertThat(underTest.toResponse(label))
            .returns(LABEL_ID, LabelResponse::getLabelId)
            .returns(LABEL, LabelResponse::getLabel);
    }
}