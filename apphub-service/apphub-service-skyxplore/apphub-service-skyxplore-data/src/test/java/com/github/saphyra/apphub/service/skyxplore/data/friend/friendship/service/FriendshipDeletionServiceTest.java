package com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.service;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ForbiddenException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
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

        Throwable ex = catchThrowable(() -> underTest.removeFriendship(FRIENDSHIP_ID, USER_ID));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.FRIENDSHIP_NOT_FOUND.name());
    }

    @Test
    public void forbiddenOperation() {
        given(friendshipDao.findById(FRIENDSHIP_ID)).willReturn(Optional.of(friendship));
        given(friendship.getFriend1()).willReturn(UUID.randomUUID());
        given(friendship.getFriend2()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.removeFriendship(FRIENDSHIP_ID, USER_ID));

        assertThat(ex).isInstanceOf(ForbiddenException.class);
        ForbiddenException exception = (ForbiddenException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_OPERATION.name());
    }

    @Test
    public void removeFriendship() {
        given(friendshipDao.findById(FRIENDSHIP_ID)).willReturn(Optional.of(friendship));
        given(friendship.getFriend1()).willReturn(USER_ID);

        underTest.removeFriendship(FRIENDSHIP_ID, USER_ID);

        verify(friendshipDao).delete(friendship);
    }
}