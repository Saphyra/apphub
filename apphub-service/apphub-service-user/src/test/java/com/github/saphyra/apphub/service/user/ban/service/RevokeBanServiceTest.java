package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BanResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RevokeBanServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BAN_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();
    private static final UUID BANNED_USER_ID = UUID.randomUUID();

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private BanDao banDao;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private BanResponseQueryService banResponseQueryService;

    @InjectMocks
    private RevokeBanService underTest;

    @Mock
    private Ban ban;

    @Mock
    private BanResponse banResponse;

    @Test
    public void blankPassword() {
        Throwable ex = catchThrowable(() -> underTest.revokeBan(USER_ID, " ", BAN_ID));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null or blank");
    }

    @Test
    public void revokeBan() {
        given(banDao.findByIdValidated(BAN_ID)).willReturn(ban);
        given(ban.getUserId()).willReturn(BANNED_USER_ID);
        given(banResponseQueryService.getBans(BANNED_USER_ID)).willReturn(banResponse);

        assertThat(underTest.revokeBan(USER_ID, PASSWORD, BAN_ID)).isEqualTo(banResponse);

        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
        then(banDao).should().delete(ban);
    }

    @Test
    public void revokeExpiredBans() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(EXPIRATION);

        underTest.revokeExpiredBans();

        verify(banDao).deleteExpired(EXPIRATION);
    }
}