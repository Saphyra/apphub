package com.github.saphyra.apphub.service.calendar.domain.occurrence.service;

import com.github.saphyra.apphub.api.calendar.model.OccurrenceStatus;
import com.github.saphyra.apphub.api.calendar.model.request.OccurrenceRequest;
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

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "date", "must not be null");
    }

    @Test
    void nullStatus() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "status", "must not be null");
    }

    @Test
    void nullRemindMeBeforeDays() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "remindMeBeforeDays", "must not be null");
    }

    @Test
    void negativeRemindMeBeforeDays() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(-1);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "remindMeBeforeDays", "too low");
    }

    @Test
    void nullNote() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getNote()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "note", "must not be null");
    }

    @Test
    void nullReminded() {
        given(request.getDate()).willReturn(DATE);
        given(request.getStatus()).willReturn(OccurrenceStatus.DONE);
        given(request.getRemindMeBeforeDays()).willReturn(0);
        given(request.getNote()).willReturn(NOTE);
        given(request.getReminded()).willReturn(null);

        ExceptionValidator.validateInvalidParam(() -> underTest.validate(request), "reminded", "must not be null");
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