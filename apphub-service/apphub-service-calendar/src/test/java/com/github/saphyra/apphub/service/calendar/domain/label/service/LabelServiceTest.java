package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LABEL = "label";
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private LabelDao labelDao;

    @Mock
    private LabelFactory labelFactory;

    @Mock
    private LabelValidator labelValidator;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private Label label;

    @InjectMocks
    private LabelService underTest;

    @Test
    void createLabel() {
        given(labelFactory.create(USER_ID, LABEL)).willReturn(label);
        given(label.getLabelId()).willReturn(LABEL_ID);

        assertThat(underTest.createLabel(USER_ID, LABEL)).isEqualTo(LABEL_ID);

        then(labelValidator).should().validate(USER_ID, LABEL);
        then(labelDao).should().save(label);
    }

    @Test
    void deleteLabel() {
        underTest.deleteLabel(USER_ID, LABEL_ID);

        then(eventLabelMappingDao).should().deleteByUserIdAndLabelId(USER_ID, LABEL_ID);
        then(labelDao).should().deleteByUserIdAndLabelId(USER_ID, LABEL_ID);
    }

    @Test
    void editLabel() {
        given(labelDao.findByIdValidated(LABEL_ID)).willReturn(label);

        underTest.editLabel(USER_ID, LABEL_ID, LABEL);

        then(labelValidator).should().validate(USER_ID, LABEL);
        then(label).should().setLabel(LABEL);
        then(labelDao).should().save(label);
    }
}