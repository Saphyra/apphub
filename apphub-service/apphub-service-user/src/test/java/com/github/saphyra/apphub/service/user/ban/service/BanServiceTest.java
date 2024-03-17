package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BanServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final UUID BANNED_USER_ID = UUID.randomUUID();

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
    }
}