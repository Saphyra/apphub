package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.LabelObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LabelQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private LabelToResponseMapper labelToResponseMapper;

    @Mock
    private LabelObjectQueryService labelObjectQueryService;

    @InjectMocks
    private LabelQueryService underTest;

    @Mock
    private Label label;

    @Mock
    private LabelResponse labelResponse;

    @Test
    void getByEventId() {
        given(labelObjectQueryService.getLabelsOfEvent(USER_ID, EVENT_ID)).willReturn(List.of(label));
        given(labelToResponseMapper.toResponse(USER_ID, List.of(label))).willReturn(List.of(labelResponse));

        assertThat(underTest.getByEventId(USER_ID, EVENT_ID)).containsExactly(labelResponse);
    }

    @Test
    void getByUserId() {
        given(labelObjectQueryService.getByUserId(USER_ID)).willReturn(Stream.of(label));
        given(labelToResponseMapper.toResponse(USER_ID, label)).willReturn(labelResponse);

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(labelResponse);
    }

    @Test
    void getLabel_found() {
        given(labelObjectQueryService.findLabel(USER_ID, LABEL_ID, Grant.VIEW)).willReturn(java.util.Optional.of(label));
        given(labelToResponseMapper.toResponse(USER_ID, label)).willReturn(labelResponse);

        assertThat(underTest.getLabel(USER_ID, LABEL_ID)).isEqualTo(labelResponse);
    }

    @Test
    void getLabel_notFound() {
        given(labelObjectQueryService.findLabel(USER_ID, LABEL_ID, Grant.VIEW)).willReturn(java.util.Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.getLabel(USER_ID, LABEL_ID));
    }
}