package com.github.saphyra.apphub.service.community.blacklist.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class BlacklistTest {
    private static final UUID BLACKLIST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();

    private final Blacklist underTest = Blacklist.builder()
        .blacklistId(BLACKLIST_ID)
        .userId(USER_ID)
        .blockedUserId(BLOCKED_USER_ID)
        .build();

    @Test
    public void getOtherUserId_userId() {
        assertThat(underTest.getOtherUserId(USER_ID)).isEqualTo(BLOCKED_USER_ID);
    }

    @Test
    public void getOtherUserId_blockedUserId() {
        assertThat(underTest.getOtherUserId(BLOCKED_USER_ID)).isEqualTo(USER_ID);
    }

    @Test
    public void getOtherUserId_anotherId() {
        Throwable ex = catchThrowable(() -> underTest.getOtherUserId(BLACKLIST_ID));

        ExceptionValidator.validateReportedException(ex, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERAL_ERROR);
    }
}