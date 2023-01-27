package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendRequestToResponseConverterTest {
    private static final UUID USER_ID_TO_CONVERT = UUID.randomUUID();
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";

    @Mock
    private AccountClientProxy accountClientProxy;

    @InjectMocks
    private FriendRequestToResponseConverter underTest;

    @Mock
    private AccountResponse accountResponse;

    @Mock
    private FriendRequest friendRequest;

    @Test
    public void convert() {
        given(friendRequest.getFriendRequestId()).willReturn(FRIEND_REQUEST_ID);
        given(accountClientProxy.getAccount(USER_ID_TO_CONVERT)).willReturn(accountResponse);
        given(accountResponse.getEmail()).willReturn(EMAIL);
        given(accountResponse.getUsername()).willReturn(USERNAME);

        FriendRequestResponse result = underTest.convert(friendRequest, USER_ID_TO_CONVERT);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
    }
}