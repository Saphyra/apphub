package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BanFactoryTest {
    private static final UUID BANNED_USER_ID = UUID.randomUUID();
    private static final String BANNED_ROLE = "banned-role";
    private static final String REASON = "reason";
    private static final UUID BAN_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final Integer DURATION = 123;

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private BanFactory underTest;

    @Test
    public void permanent() {
        given(idGenerator.randomUuid()).willReturn(BAN_ID);

        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(BANNED_ROLE)
            .permanent(true)
            .reason(REASON)
            .build();

        Ban result = underTest.create(USER_ID, request);

        assertThat(result.getId()).isEqualTo(BAN_ID);
        assertThat(result.getUserId()).isEqualTo(BANNED_USER_ID);
        assertThat(result.getBannedRole()).isEqualTo(BANNED_ROLE);
        assertThat(result.getExpiration()).isNull();
        assertThat(result.getPermanent()).isTrue();
        assertThat(result.getReason()).isEqualTo(REASON);
        assertThat(result.getBannedBy()).isEqualTo(USER_ID);
    }

    @Test
    public void temporary() {
        given(idGenerator.randomUuid()).willReturn(BAN_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_DATE);

        BanRequest request = BanRequest.builder()
            .bannedUserId(BANNED_USER_ID)
            .bannedRole(BANNED_ROLE)
            .permanent(false)
            .duration(DURATION)
            .reason(REASON)
            .chronoUnit("DAYS")
            .build();

        Ban result = underTest.create(USER_ID, request);

        assertThat(result.getId()).isEqualTo(BAN_ID);
        assertThat(result.getUserId()).isEqualTo(BANNED_USER_ID);
        assertThat(result.getBannedRole()).isEqualTo(BANNED_ROLE);
        assertThat(result.getExpiration()).isEqualTo(CURRENT_DATE.plusDays(DURATION));
        assertThat(result.getPermanent()).isFalse();
        assertThat(result.getReason()).isEqualTo(REASON);
        assertThat(result.getBannedBy()).isEqualTo(USER_ID);
    }
}