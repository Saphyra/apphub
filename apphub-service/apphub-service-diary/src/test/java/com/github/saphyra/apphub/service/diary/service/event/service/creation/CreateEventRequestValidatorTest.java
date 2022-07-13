package com.github.saphyra.apphub.service.diary.service.event.service.creation;

import com.github.saphyra.apphub.api.diary.model.CreateEventRequest;
import com.github.saphyra.apphub.api.diary.model.RepetitionType;
import com.github.saphyra.apphub.service.diary.service.event.service.EventTitleValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CreateEventRequestValidatorTest {
    private static final String TITLE = "title";
    private static final Integer REPETITION_DAYS = 435;
    private static final LocalDate DATE = LocalDate.now();

    @Mock
    private EventTitleValidator eventTitleValidator;

    @InjectMocks
    private CreateEventRequestValidator underTest;

    @After
    public void validate() {
        verify(eventTitleValidator).validate(TITLE);
    }

    @Test
    public void nullReferenceDate() {
        CreateEventRequest request = validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .referenceDate(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "referenceDate", "must not be null");
    }

    @Test
    public void nullDate() {
        CreateEventRequest request = validRequest(RepetitionType.ONE_TIME)
            .toBuilder()
            .date(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "date", "must not be null");
    }

    @Test
    public void nullRepetitionType() {
        CreateEventRequest request = validRequest(null);

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "repetitionType", "must not be null");
    }

    @Test
    public void emptyRepetitionDaysOfWeek() {
        CreateEventRequest request = validRequest(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionDaysOfWeek(Collections.emptyList())
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "repetitionDaysOfWeek", "must not be empty");
    }

    @Test
    public void tooLowRepetitionDays() {
        CreateEventRequest request = validRequest(RepetitionType.EVERY_X_DAYS)
            .toBuilder()
            .repetitionDays(0)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "repetitionDays", "too low");
    }

    @Test
    public void validDaysOfWeek() {
        underTest.validate(validRequest(RepetitionType.DAYS_OF_WEEK));
    }

    @Test
    public void validEveryXDays() {
        underTest.validate(validRequest(RepetitionType.EVERY_X_DAYS));
    }

    @Test
    public void validOneTime() {
        underTest.validate(validRequest(RepetitionType.ONE_TIME));
    }

    private CreateEventRequest validRequest(RepetitionType repetitionType) {
        return CreateEventRequest.builder()
            .referenceDate(DATE)
            .date(DATE)
            .title(TITLE)
            .repetitionType(repetitionType)
            .repetitionDays(REPETITION_DAYS)
            .repetitionDaysOfWeek(List.of(DayOfWeek.MONDAY))
            .build();
    }
}