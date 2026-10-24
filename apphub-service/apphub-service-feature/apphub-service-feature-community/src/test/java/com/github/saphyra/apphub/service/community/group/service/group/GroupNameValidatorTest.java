package com.github.saphyra.apphub.service.community.group.service.group;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowable;

public class GroupNameValidatorTest {
    private final GroupNameValidator underTest = new GroupNameValidator();

    @Test
    public void nullInput() {
        Throwable ex = catchThrowable(() -> underTest.validate(null));

        ExceptionValidator.validateInvalidParam(ex, "groupName", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void tooShort() {
        Throwable ex = catchThrowable(() -> underTest.validate("as"));

        ExceptionValidator.validateInvalidParam(ex, "groupName", ValidationUtil.TOO_SHORT);
    }

    @Test
    public void tooLong() {
        Throwable ex = catchThrowable(() -> underTest.validate(Stream.generate(() -> "a").limit(31).collect(Collectors.joining())));

        ExceptionValidator.validateInvalidParam(ex, "groupName", ValidationUtil.TOO_LONG);
    }

    @Test
    public void valid() {
        underTest.validate("asd");
    }
}