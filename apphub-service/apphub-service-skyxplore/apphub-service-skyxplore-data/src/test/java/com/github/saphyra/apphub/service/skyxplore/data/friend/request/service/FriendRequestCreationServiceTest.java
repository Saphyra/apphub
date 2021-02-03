package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ConflictException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
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
public class FriendRequestCreationServiceTest {
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final UUID SENDER_ID = UUID.randomUUID();

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private CharacterDao characterDao;

    @Mock
    private FriendRequestFactory friendRequestFactory;

    @InjectMocks
    private FriendRequestCreationService underTest;

    @Mock
    private SkyXploreCharacter character;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private Friendship friendship;

    @Test
    public void characterNotFound() {
        given(characterDao.findById(FRIEND_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.createFriendRequest(SENDER_ID, FRIEND_ID));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    public void friendRequestAlreadyExists() {
        given(characterDao.findById(FRIEND_ID)).willReturn(Optional.of(character));
        given(friendRequestDao.findBySenderIdAndFriendId(SENDER_ID, FRIEND_ID)).willReturn(Optional.of(friendRequest));

        Throwable ex = catchThrowable(() -> underTest.createFriendRequest(SENDER_ID, FRIEND_ID));

        assertThat(ex).isInstanceOf(ConflictException.class);
        ConflictException exception = (ConflictException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.FRIEND_REQUEST_ALREADY_EXISTS.name());
    }

    @Test
    public void friendshipAlreadyExists() {
        given(characterDao.findById(FRIEND_ID)).willReturn(Optional.of(character));
        given(friendRequestDao.findBySenderIdAndFriendId(SENDER_ID, FRIEND_ID)).willReturn(Optional.empty());
        given(friendshipDao.findByFriendIds(SENDER_ID, FRIEND_ID)).willReturn(Optional.of(friendship));

        Throwable ex = catchThrowable(() -> underTest.createFriendRequest(SENDER_ID, FRIEND_ID));

        assertThat(ex).isInstanceOf(ConflictException.class);
        ConflictException exception = (ConflictException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.FRIENDSHIP_ALREADY_EXISTS.name());
    }

    @Test
    public void createFriendRequest() {
        given(characterDao.findById(FRIEND_ID)).willReturn(Optional.of(character));
        given(friendRequestDao.findBySenderIdAndFriendId(SENDER_ID, FRIEND_ID)).willReturn(Optional.empty());
        given(friendshipDao.findByFriendIds(SENDER_ID, FRIEND_ID)).willReturn(Optional.empty());
        given(friendRequestFactory.create(SENDER_ID, FRIEND_ID)).willReturn(friendRequest);

        underTest.createFriendRequest(SENDER_ID, FRIEND_ID);

        verify(friendRequestDao).save(friendRequest);
    }
}