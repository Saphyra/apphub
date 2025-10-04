package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.calendar.domain.label.dao.LabelDao;
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

    @Mock
    private LabelDao labelDao;

    @InjectMocks
    private LabelIdValidator underTest;

    @Test
    void nullLabels() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(null), "labels", "must not be null");
    }

    @Test
    void nullInLabels() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(CollectionUtils.toList(LABEL_ID, null)), "labels", "must not contain null values");
    }

    @Test
    void doesNotExist() {
        given(labelDao.existsById(LABEL_ID)).willReturn(false);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(List.of(LABEL_ID)), "field", "Label with id " + LABEL_ID + " does not exist");
    }

    @Test
    void valid() {
        given(labelDao.existsById(LABEL_ID)).willReturn(true);

        underTest.validate(List.of(LABEL_ID));
    }
}