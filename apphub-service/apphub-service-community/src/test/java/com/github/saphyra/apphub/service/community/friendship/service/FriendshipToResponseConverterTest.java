package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.api.user.model.account.AccountResponse;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendshipToResponseConverterTest {
    private static final UUID USER_ID_TO_CONVERT = UUID.randomUUID();
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";

    @Mock
    private AccountClientProxy accountClientProxy;

    @InjectMocks
    private FriendshipToResponseConverter underTest;

    @Mock
    private Friendship friendship;

    @Mock
    private AccountResponse accountResponse;

    @Test
    public void convert() {
        given(accountClientProxy.getAccount(USER_ID_TO_CONVERT)).willReturn(accountResponse);
        given(friendship.getFriendshipId()).willReturn(FRIENDSHIP_ID);
        given(accountResponse.getEmail()).willReturn(EMAIL);
        given(accountResponse.getUsername()).willReturn(USERNAME);

        FriendshipResponse result = underTest.convert(friendship, USER_ID_TO_CONVERT);

        assertThat(result.getFriendshipId()).isEqualTo(FRIENDSHIP_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
    }
}