package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LabelToResponseMapperTest {
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL = "label";
    private static final UUID USER_ID = UUID.randomUUID();

    @InjectMocks
    private LabelToResponseMapper underTest;

    @Test
    void toResponse() {
        Label label = Label.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        assertThat(underTest.toResponse(label, true))
            .returns(LABEL_ID, LabelResponse::getLabelId)
            .returns(USER_ID, LabelResponse::getUserId)
            .returns(LABEL, LabelResponse::getLabel)
            .returns(true, LabelResponse::getShared);
    }
}