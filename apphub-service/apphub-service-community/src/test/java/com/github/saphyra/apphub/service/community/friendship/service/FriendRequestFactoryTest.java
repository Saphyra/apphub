package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class FriendRequestFactoryTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();
    private static final UUID RECEIVER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private FriendRequestFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(FRIEND_REQUEST_ID);

        FriendRequest result = underTest.create(SENDER_ID, RECEIVER_ID);

        assertThat(result.getFriendRequestId()).isEqualTo(FRIEND_REQUEST_ID);
        assertThat(result.getSenderId()).isEqualTo(SENDER_ID);
        assertThat(result.getReceiverId()).isEqualTo(RECEIVER_ID);
    }
}