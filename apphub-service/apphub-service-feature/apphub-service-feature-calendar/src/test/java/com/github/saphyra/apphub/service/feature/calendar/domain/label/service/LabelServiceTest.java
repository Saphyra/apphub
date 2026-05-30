package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelFactory;
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
    private CommonCalendarDao commonCalendarDao;

    @InjectMocks
    private LabelService underTest;

    @Mock
    private Label label;

    @Test
    void createLabel() {
        given(labelFactory.create(LABEL)).willReturn(label);
        given(label.getLabelId()).willReturn(LABEL_ID);

        assertThat(underTest.createLabel(USER_ID, LABEL)).isEqualTo(LABEL_ID);

        then(labelValidator).should().validate(USER_ID, LABEL);
        then(labelDao).should().save(USER_ID, label);
    }

    @Test
    void deleteLabel() {
        underTest.deleteLabel(USER_ID, LABEL_ID);

        then(commonCalendarDao).should().deleteLabel(USER_ID, LABEL_ID);
    }

    @Test
    void editLabel() {
        given(labelValidator.validate(USER_ID, LABEL)).willReturn(java.util.List.of(label));
        given(label.getLabelId()).willReturn(LABEL_ID);

        underTest.editLabel(USER_ID, LABEL_ID, LABEL);

        then(label).should().setLabel(LABEL);
        then(labelDao).should().save(USER_ID, label);
    }
}