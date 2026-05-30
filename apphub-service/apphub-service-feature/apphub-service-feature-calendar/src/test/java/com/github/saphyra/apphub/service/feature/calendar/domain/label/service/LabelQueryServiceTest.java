package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LabelQueryServiceTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private LabelDao labelDao;

    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelQueryService underTest;

    @Mock
    private Label label;

    @Mock
    private LabelResponse labelResponse;

    @Test
    void getByEventId() {
        given(eventLabelMappingDao.getByEventId(USER_ID, EVENT_ID)).willReturn(List.of(LABEL_ID));
        given(labelDao.getByLabelIds(USER_ID, List.of(LABEL_ID))).willReturn(List.of(label));
        given(labelMapper.toResponse(List.of(label))).willReturn(List.of(labelResponse));

        assertThat(underTest.getByEventId(USER_ID, EVENT_ID)).containsExactly(labelResponse);
    }

    @Test
    void getByUserId() {
        given(labelDao.getByUserId(USER_ID)).willReturn(List.of(label));
        given(labelMapper.toResponse(label)).willReturn(labelResponse);

        assertThat(underTest.getByUserId(USER_ID)).containsExactly(labelResponse);
    }

    @Test
    void getLabel() {
        given(labelDao.findByIdValidated(USER_ID, LABEL_ID)).willReturn(label);
        given(labelMapper.toResponse(label)).willReturn(labelResponse);

        assertThat(underTest.getLabel(USER_ID, LABEL_ID)).isEqualTo(labelResponse);
    }
}