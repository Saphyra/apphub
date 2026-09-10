package com.github.saphyra.apphub.service.feature.calendar.domain.label.service;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LabelValidatorTest {
    private static final String LABEL = "label";

    @InjectMocks
    private LabelValidator underTest;

    @Test
    void blankLabel() {
        ExceptionValidator.validateInvalidParam(() -> underTest.validate(""), "label", "must not be null or blank");
    }

    @Test
    void tooLongLabel() {
        String label = "a".repeat(256);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(label), "label", "too long");
    }

    @Test
    void valid(){
        underTest.validate(LABEL);
    }
}