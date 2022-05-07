package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RevokeBanServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BAN_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private BanDao banDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private RevokeBanService underTest;

    @Test
    public void blankPassword() {
        Throwable ex = catchThrowable(() -> underTest.revokeBan(USER_ID, " ", BAN_ID));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null or blank");
    }

    @Test
    public void revokeBan() {
        underTest.revokeBan(USER_ID, PASSWORD, BAN_ID);

        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
        verify(banDao).deleteById(BAN_ID);
    }

    @Test
    public void revokeExpiredBans() {
        given(dateTimeUtil.getCurrentTime()).willReturn(EXPIRATION);

        underTest.revokeExpiredBans();

        verify(banDao).deleteExpired(EXPIRATION);
    }
}