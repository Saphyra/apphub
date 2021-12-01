package com.github.saphyra.apphub.service.user.ban;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.ban.service.BanResponseQueryService;
import com.github.saphyra.apphub.service.user.ban.service.BanService;
import com.github.saphyra.apphub.service.user.ban.service.RevokeBanService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BanControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final UUID BAN_ID = UUID.randomUUID();
    private static final UUID BANNED_USER_ID = UUID.randomUUID();

    @Mock
    private BanService banService;

    @Mock
    private RevokeBanService revokeBanService;

    @Mock
    private BanResponseQueryService banResponseQueryService;

    @InjectMocks
    private BanControllerImpl underTest;

    @Mock
    private BanRequest request;

    @Mock
    private BanResponse response;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void banUser() {
        underTest.banUser(request, accessTokenHeader);

        verify(banService).ban(USER_ID, request);
    }

    @Test
    public void revokeBan() {
        underTest.revokeBan(new OneParamRequest<>(PASSWORD), BAN_ID, accessTokenHeader);

        verify(revokeBanService).revokeBan(USER_ID, PASSWORD, BAN_ID);
    }

    @Test
    public void getBans() {
        given(banResponseQueryService.getBans(BANNED_USER_ID)).willReturn(List.of(response));

        List<BanResponse> result = underTest.getBans(BANNED_USER_ID, accessTokenHeader);

        assertThat(result).containsExactly(response);
    }
}