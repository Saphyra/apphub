package com.github.saphyra.apphub.service.notebook.service.pin.group;

import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class PinGroupNameValidatorTest {
    @InjectMocks
    private PinGroupNameValidator underTest;

    @Test
    void blankName() {
        Throwable ex = catchThrowable(() -> underTest.validate(" "));

        ExceptionValidator.validateInvalidParam(ex, "pinGroupName", "must not be null or blank");
    }

    @Test
    void tooLong() {
        Throwable ex = catchThrowable(() -> underTest.validate(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "pinGroupName", "too long");
    }

    @Test
    void valid() {
        underTest.validate("asd");
    }
}