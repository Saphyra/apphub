package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ForbiddenException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FriendRequestCancelServiceTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FriendRequestDao friendRequestDao;

    @InjectMocks
    private FriendRequestCancelService underTest;

    @Mock
    private FriendRequest friendRequest;

    @Test
    public void friendRequestNotFound() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.cancelFriendRequest(USER_ID, FRIEND_REQUEST_ID));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.FRIEND_REQUEST_NOT_FOUND.name());
    }

    @Test
    public void forbiddenOperation() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.of(friendRequest));
        given(friendRequest.getFriendId()).willReturn(UUID.randomUUID());
        given(friendRequest.getSenderId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.cancelFriendRequest(USER_ID, FRIEND_REQUEST_ID));

        assertThat(ex).isInstanceOf(ForbiddenException.class);
        ForbiddenException exception = (ForbiddenException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
    }

    @Test
    public void cancelFriendRequest() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.of(friendRequest));
        given(friendRequest.getFriendId()).willReturn(USER_ID);

        underTest.cancelFriendRequest(USER_ID, FRIEND_REQUEST_ID);

        verify(friendRequestDao).delete(friendRequest);
    }
}