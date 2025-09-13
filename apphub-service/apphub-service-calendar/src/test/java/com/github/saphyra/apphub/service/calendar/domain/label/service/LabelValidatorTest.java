package com.github.saphyra.apphub.service.calendar.domain.label.service;

import com.github.saphyra.apphub.service.calendar.domain.label.dao.Label;
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
class LabelValidatorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LABEL = "label";

    @Mock
    private LabelDao labelDao;

    @InjectMocks
    private LabelValidator underTest;

    @Mock
    private Label label;

    @Test
    void blankLabel() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, ""), "label", "must not be null or blank");
    }

    @Test
    void tooLongLabel() {
        String label = "a".repeat(256);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, label), "label", "too long");
    }

    @Test
    void alreadyExists() {
        given(labelDao.getByUserId(USER_ID)).willReturn(List.of(label));
        given(label.getLabel()).willReturn(LABEL);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(USER_ID, LABEL), "label", "already exists");
    }

    @Test
    void valid(){
        given(labelDao.getByUserId(USER_ID)).willReturn(List.of(label));
        given(label.getLabel()).willReturn("other-label");

        underTest.validate(USER_ID, LABEL);
    }
}