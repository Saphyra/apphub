package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.etc.user.model.ban.BanRequest;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class BanRequestValidatorTest {
    private static final UUID BANNED_USER_ID = UUID.randomUUID();
    private static final String REASON = "reason";
    private static final String PASSWORD = "password";

    @InjectMocks
    private BanRequestValidator underTest;

    @Test
    public void valid_permanent() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(true)
            .duration(null)
            .chronoUnit(null)
            .reason(REASON)
            .password(PASSWORD)
            .build();

        underTest.validate(request);
    }

    @Test
    public void valid_temporary() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.DAYS.name())
            .reason(REASON)
            .password(PASSWORD)
            .build();

        underTest.validate(request);
    }

    @Test
    public void nullBannedUserId() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(null)
            .bannedRole(Role.TEST)
            .permanent(true)
            .duration(null)
            .chronoUnit(null)
            .reason(REASON)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "bannedUserId", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void nullBannedRole() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(null)
            .permanent(true)
            .duration(null)
            .chronoUnit(null)
            .reason(REASON)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "bannedRole", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void nullPermanent() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(null)
            .duration(null)
            .chronoUnit(null)
            .reason(REASON)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "permanent", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void blankReason() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(true)
            .duration(null)
            .chronoUnit(null)
            .reason(" ")
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "reason", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    public void blankPassword() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(true)
            .duration(null)
            .chronoUnit(null)
            .reason(REASON)
            .password(" ")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "password", ValidationUtil.MUST_NOT_BE_NULL_OR_BLANK);
    }

    @Test
    public void nullDurationWhenTemporary() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(false)
            .duration(null)
            .chronoUnit(ChronoUnit.DAYS.name())
            .reason(REASON)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "duration", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void durationTooLowWhenTemporary() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(false)
            .duration(0)
            .chronoUnit(ChronoUnit.DAYS.name())
            .reason(REASON)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "duration", ValidationUtil.TOO_LOW);
    }

    @Test
    public void nullChronoUnitWhenTemporary() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(null)
            .reason(REASON)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "chronoUnit", ValidationUtil.MUST_NOT_BE_NULL);
    }

    @Test
    public void invalidValueForChronoUnitWhenTemporary() {
        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(Role.TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit("asd")
            .reason(REASON)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "chronoUnit", ValidationUtil.INVALID_VALUE);
    }
}