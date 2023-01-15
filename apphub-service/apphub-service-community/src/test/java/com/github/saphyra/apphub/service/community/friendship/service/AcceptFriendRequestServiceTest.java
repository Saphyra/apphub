package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequest;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AcceptFriendRequestServiceTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private FriendshipFactory friendshipFactory;

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private FriendshipToResponseConverter friendshipToResponseConverter;

    @InjectMocks
    private AcceptFriendRequestService underTest;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private Friendship friendship;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Test
    public void friendRequestNotFound() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.accept(USER_ID, FRIEND_REQUEST_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void forbiddenOperation() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.of(friendRequest));
        given(friendRequest.getReceiverId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.accept(USER_ID, FRIEND_REQUEST_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void accept() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.of(friendRequest));
        given(friendRequest.getReceiverId()).willReturn(USER_ID);
        given(friendRequest.getSenderId()).willReturn(SENDER_ID);
        given(friendshipFactory.create(SENDER_ID, USER_ID)).willReturn(friendship);
        given(friendshipToResponseConverter.convert(friendship, SENDER_ID)).willReturn(friendshipResponse);

        FriendshipResponse result = underTest.accept(USER_ID, FRIEND_REQUEST_ID);

        verify(friendshipDao).save(friendship);
        verify(friendRequestDao).delete(friendRequest);

        assertThat(result).isEqualTo(friendshipResponse);
    }
}