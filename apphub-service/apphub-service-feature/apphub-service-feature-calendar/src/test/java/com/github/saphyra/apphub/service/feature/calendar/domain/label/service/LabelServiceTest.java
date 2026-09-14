package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.response.LabelResponse;
import com.github.saphyra.apphub.service.feature.calendar.common.dao.CommonCalendarDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelFactory;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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

    @Mock
    private LabelToResponseMapper labelToResponseMapper;

    @Mock
    private LabelObjectQueryService labelObjectQueryService;

    @InjectMocks
    private LabelService underTest;

    @Mock
    private Label label;

    @Mock
    private LabelResponse labelResponse;

    @Test
    void createLabel() {
        given(labelFactory.create(USER_ID, LABEL)).willReturn(label);
        given(labelToResponseMapper.toResponse(USER_ID, label)).willReturn(labelResponse);

        assertThat(underTest.createLabel(USER_ID, LABEL)).isEqualTo(labelResponse);

        then(labelValidator).should().validate(LABEL);
        then(commonCalendarDao).should().saveLabel(label);
    }

    @Test
    void deleteLabel() {
        given(labelObjectQueryService.findLabel(USER_ID, LABEL_ID, Grant.DELETE)).willReturn(Optional.of(label));

        underTest.deleteLabel(USER_ID, LABEL_ID);

        then(commonCalendarDao).should().deleteLabel(label.getUserId(), label.getLabelId());
    }

    @Test
    void deleteLabel_notFound() {
        given(labelObjectQueryService.findLabel(USER_ID, LABEL_ID, Grant.DELETE)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.deleteLabel(USER_ID, LABEL_ID));
    }

    @Test
    void editLabel_found() {
        given(labelObjectQueryService.findLabel(USER_ID, LABEL_ID, Grant.VIEW, Grant.EDIT)).willReturn(java.util.Optional.of(label));

        underTest.editLabel(USER_ID, LABEL_ID, LABEL);

        then(label).should().setLabel(LABEL);
        then(labelValidator).should().validate(LABEL);
        then(labelDao).should().save(label);
    }

    @Test
    void editLabel_notFound() {
        given(labelObjectQueryService.findLabel(USER_ID, LABEL_ID, Grant.VIEW, Grant.EDIT)).willReturn(java.util.Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.editLabel(USER_ID, LABEL_ID, LABEL));

        then(labelValidator).should().validate(LABEL);
    }
}