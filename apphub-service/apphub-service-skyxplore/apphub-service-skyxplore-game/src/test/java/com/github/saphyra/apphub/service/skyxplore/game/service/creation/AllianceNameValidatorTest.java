package com.github.saphyra.apphub.service.skyxplore.game.service.creation;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class AllianceNameValidatorTest {
    @InjectMocks
    private AllianceNameValidator underTest;

    @Test
    public void nullAllianceName() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        ExceptionValidator.validateInvalidParam(ex, "allianceName", "must not be null");
    }

    @Test
    public void allianceNameEmpty() {
        Throwable ex = catchThrowable(() -> underTest.validate(""));

        ExceptionValidator.validateInvalidParam(ex, "allianceName", "empty");
    }

    @Test
    public void allianceNameTooLong() {
        Throwable ex = catchThrowable(() -> underTest.validate(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "allianceName", "too long");
    }

    @Test
    public void valid() {
        underTest.validate("asd");
    }
}