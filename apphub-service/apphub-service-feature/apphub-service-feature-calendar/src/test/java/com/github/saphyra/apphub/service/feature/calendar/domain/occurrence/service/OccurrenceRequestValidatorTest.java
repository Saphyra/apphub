package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.feature.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.feature.calendar.model.request.OccurrenceRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OccurrenceRequestValidatorTest {
    private static final LocalDate DATE = LocalDate.now();
    private static final String NOTE = "note";

    @InjectMocks
    private OccurrenceRequestValidator underTest;

    @Mock
    private OccurrenceRequest request;

    @Test
    void nullDate() {
        given(request.getDate()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "date", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullStatus() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "status", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullRemindMeBeforeDays() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "remindMeBeforeDays", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void negativeRemindMeBeforeDays() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "remindMeBeforeDays", ValidationUtil.TOO_LOW);
    }

    @Test
    void nullNote() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getNote()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "note", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void nullReminded() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getNote()).willReturn(NOTE);
        given(request.getReminded()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "reminded", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    void valid() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getNote()).willReturn(NOTE);
        given(request.getReminded()).willReturn(false);

        underTest.validate(request);
    }
}