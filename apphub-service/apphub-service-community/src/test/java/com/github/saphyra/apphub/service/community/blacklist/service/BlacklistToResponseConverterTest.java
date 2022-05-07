package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.api.community.model.response.blacklist.BlacklistResponse;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BlacklistToResponseConverterTest {
    private static final UUID BLACKLIST_ID = UUID.randomUUID();
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";

    @Mock
    private AccountClientProxy accountClientProxy;

    @InjectMocks
    private BlacklistToResponseConverter underTest;

    @Test
    public void convert() {
        Blacklist blacklist = Blacklist.builder()
            .blacklistId(BLACKLIST_ID)
            .blockedUserId(BLOCKED_USER_ID)
            .build();
        AccountResponse accountResponse = AccountResponse.builder()
            .username(USERNAME)
            .email(EMAIL)
            .build();

        given(accountClientProxy.getAccount(BLOCKED_USER_ID)).willReturn(accountResponse);

        BlacklistResponse result = underTest.convert(blacklist);

        assertThat(result.getBlacklistId()).isEqualTo(BLACKLIST_ID);
        assertThat(result.getBlockedUserId()).isEqualTo(BLOCKED_USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
    }
}