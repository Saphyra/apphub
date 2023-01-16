package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
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

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FriendRequestDeletionServiceTest {
    private static final UUID FRIEND_REQUEST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FriendRequestDao friendRequestDao;

    @InjectMocks
    private FriendRequestDeletionService underTest;

    @Mock
    private FriendRequest friendRequest;

    @Test
    public void friendRequestNotFound() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.delete(USER_ID, FRIEND_REQUEST_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void forbiddenOperation() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.of(friendRequest));
        given(friendRequest.getSenderId()).willReturn(UUID.randomUUID());
        given(friendRequest.getReceiverId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.delete(USER_ID, FRIEND_REQUEST_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void deleteFriendRequest() {
        given(friendRequestDao.findById(FRIEND_REQUEST_ID)).willReturn(Optional.of(friendRequest));
        given(friendRequest.getSenderId()).willReturn(USER_ID);

        underTest.delete(USER_ID, FRIEND_REQUEST_ID);

        verify(friendRequestDao).delete(friendRequest);
    }
}