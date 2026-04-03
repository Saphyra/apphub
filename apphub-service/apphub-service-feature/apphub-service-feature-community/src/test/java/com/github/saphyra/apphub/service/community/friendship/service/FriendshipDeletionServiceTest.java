package com.github.saphyra.apphub.service.community.friendship.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.Friendship;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
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
public class FriendshipDeletionServiceTest {
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FriendshipDao friendshipDao;

    @InjectMocks
    private FriendshipDeletionService underTest;

    @Mock
    private Friendship friendship;

    @Test
    public void friendshipNotFound() {
        given(friendshipDao.findById(FRIENDSHIP_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.delete(USER_ID, FRIENDSHIP_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void forbiddenOperation() {
        given(friendshipDao.findById(FRIENDSHIP_ID)).willReturn(Optional.of(friendship));
        given(friendship.getUserId()).willReturn(UUID.randomUUID());
        given(friendship.getFriendId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.delete(USER_ID, FRIENDSHIP_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void delete() {
        given(friendshipDao.findById(FRIENDSHIP_ID)).willReturn(Optional.of(friendship));
        given(friendship.getUserId()).willReturn(USER_ID);

        underTest.delete(USER_ID, FRIENDSHIP_ID);

        verify(friendshipDao).delete(friendship);
    }
}