package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.api.community.model.response.friend_request.FriendRequestResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
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
public class FriendRequestCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID RECEIVER_ID = UUID.randomUUID();

    @Mock
    private BlacklistDao blacklistDao;

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private FriendRequestFactory friendRequestFactory;

    @Mock
    private FriendRequestToResponseConverter friendRequestToResponseConverter;

    @Mock
    private AccountClientProxy accountClientProxy;

    @InjectMocks
    private FriendRequestCreationService underTest;

    @Mock
    private Blacklist blacklist;

    @Mock
    private Friendship friendship;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private FriendRequestResponse friendRequestResponse;

    @Test
    public void userDoesNotExist() {
        given(accountClientProxy.userExists(RECEIVER_ID)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, RECEIVER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void userBlocked() {
        given(accountClientProxy.userExists(RECEIVER_ID)).willReturn(true);
        given(blacklistDao.findByUserIdOrBlockedUserId(USER_ID, RECEIVER_ID)).willReturn(Optional.of(blacklist));

        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, RECEIVER_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void friendshipAlreadyExists() {
        given(accountClientProxy.userExists(RECEIVER_ID)).willReturn(true);
        given(blacklistDao.findByUserIdOrBlockedUserId(USER_ID, RECEIVER_ID)).willReturn(Optional.empty());
        given(friendRequestDao.findBySenderIdAndReceiverId(USER_ID, RECEIVER_ID)).willReturn(Optional.of(friendRequest));

        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, RECEIVER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void friendRequestAlreadyExists() {
        given(accountClientProxy.userExists(RECEIVER_ID)).willReturn(true);
        given(blacklistDao.findByUserIdOrBlockedUserId(USER_ID, RECEIVER_ID)).willReturn(Optional.empty());
        given(friendRequestDao.findBySenderIdAndReceiverId(USER_ID, RECEIVER_ID)).willReturn(Optional.empty());
        given(friendshipDao.findByUserIdAndFriendId(USER_ID, RECEIVER_ID)).willReturn(Optional.of(friendship));

        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, RECEIVER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void create() {
        given(accountClientProxy.userExists(RECEIVER_ID)).willReturn(true);
        given(blacklistDao.findByUserIdOrBlockedUserId(USER_ID, RECEIVER_ID)).willReturn(Optional.empty());
        given(friendRequestDao.findBySenderIdAndReceiverId(USER_ID, RECEIVER_ID)).willReturn(Optional.empty());
        given(friendshipDao.findByUserIdAndFriendId(USER_ID, RECEIVER_ID)).willReturn(Optional.empty());

        given(friendRequestFactory.create(USER_ID, RECEIVER_ID)).willReturn(friendRequest);
        given(friendRequestToResponseConverter.convert(friendRequest, RECEIVER_ID)).willReturn(friendRequestResponse);

        FriendRequestResponse result = underTest.create(USER_ID, RECEIVER_ID);

        verify(friendRequestDao).save(friendRequest);

        assertThat(result).isEqualTo(friendRequestResponse);
    }
}