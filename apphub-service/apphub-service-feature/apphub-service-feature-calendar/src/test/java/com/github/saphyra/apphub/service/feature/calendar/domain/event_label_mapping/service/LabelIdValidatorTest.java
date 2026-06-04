package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LabelIdValidatorTest {
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private LabelDao labelDao;

    @InjectMocks
    private LabelIdValidator underTest;

    @Mock
    private Label label;

    @Test
    void nullLabels() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, null), "labels", "must not be null");
    }

    @Test
    void nullInLabels() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, CollectionUtils.toList(LABEL_ID, null)), "labels", "must not contain null values");
    }

    @Test
    void doesNotExist() {
        given(labelDao.getByLabelIds(USER_ID, List.of(LABEL_ID))).willReturn(List.of());

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, List.of(LABEL_ID)), "labels", "Unsupported values: " + List.of(LABEL_ID));
    }

    @Test
    void valid() {
        given(labelDao.getByLabelIds(USER_ID, List.of(LABEL_ID))).willReturn(List.of(label));
        given(label.getLabelId()).willReturn(LABEL_ID);

        underTest.validate(USER_ID, List.of(LABEL_ID));
    }
}