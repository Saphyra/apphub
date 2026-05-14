package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.etc.user.model.ban.BanRequest;
import com.github.saphyra.apphub.api.etc.user.model.ban.BanResponse;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.api.platform.authorization.client.AuthorizationClient;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BanServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final UUID BANNED_USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    @Mock
    private BanRequestValidator banRequestValidator;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private BanFactory banFactory;

    @Mock
    private BanDao banDao;

    @Mock
    private BanResponseQueryService banResponseQueryService;

    @Mock
    private AuthorizationClient authorizationClient;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private BanService underTest;

    @Mock
    private BanRequest request;

    @Mock
    private Ban ban;

    @Mock
    private BanResponse banResponse;

    @Test
    public void ban() {
        given(request.getPassword()).willReturn(PASSWORD);
        given(banFactory.create(USER_ID, request)).willReturn(ban);
        given(request.getBannedUserId()).willReturn(BANNED_USER_ID);
        given(banResponseQueryService.getBans(BANNED_USER_ID)).willReturn(banResponse);

        assertThat(underTest.ban(USER_ID, request)).isEqualTo(banResponse);

        verify(banRequestValidator).validate(request);
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
        verify(banDao).save(ban);
        verify(authorizationClient).invalidateAllAccessTokens(BANNED_USER_ID);
    }

    @Test
    public void getActivelyBannedRolesOf_permanentBan() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(ban.isPermanent()).willReturn(true);
        given(ban.getBannedRole()).willReturn(Role.TEST);
        given(banDao.getByUserId(USER_ID)).willReturn(List.of(ban));

        List<Role> result = underTest.getActivelyBannedRolesOf(USER_ID);

        assertThat(result).containsExactly(Role.TEST);
    }

    @Test
    public void getActivelyBannedRolesOf_activeBan() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(ban.isPermanent()).willReturn(false);
        given(ban.getExpiration()).willReturn(CURRENT_TIME.plusHours(1));
        given(ban.getBannedRole()).willReturn(Role.TEST);
        given(banDao.getByUserId(USER_ID)).willReturn(List.of(ban));

        List<Role> result = underTest.getActivelyBannedRolesOf(USER_ID);

        assertThat(result).containsExactly(Role.TEST);
    }

    @Test
    public void getActivelyBannedRolesOf_expiredBan() {
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CURRENT_TIME);
        given(ban.isPermanent()).willReturn(false);
        given(ban.getExpiration()).willReturn(CURRENT_TIME.minusHours(1));
        given(banDao.getByUserId(USER_ID)).willReturn(List.of(ban));

        List<Role> result = underTest.getActivelyBannedRolesOf(USER_ID);

        assertThat(result).isEmpty();
    }
}