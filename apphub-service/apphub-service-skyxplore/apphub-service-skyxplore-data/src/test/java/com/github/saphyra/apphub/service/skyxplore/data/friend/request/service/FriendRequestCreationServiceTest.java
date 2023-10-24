package com.github.saphyra.apphub.service.skyxplore.data.friend.request.service;

import com.github.saphyra.apphub.api.skyxplore.response.friendship.IncomingFriendRequestResponse;
import com.github.saphyra.apphub.api.skyxplore.response.friendship.SentFriendRequestResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.converter.FriendRequestToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import com.github.saphyra.apphub.service.skyxplore.data.ws.SkyXploreFriendshipWebSocketHandler;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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

    @Mock
    private SkyXploreFriendshipWebSocketHandler friendshipWebSocketHandler;

    @Mock
    private FriendRequestToResponseConverter friendRequestToResponseConverter;

    @InjectMocks
    private FriendRequestCreationService underTest;

    @Mock
    private SkyXploreCharacter character;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private Friendship friendship;

    @Mock
    private SentFriendRequestResponse sentFriendRequestResponse;

    @Mock
    private IncomingFriendRequestResponse incomingFriendRequestResponse;

    @Test
    public void characterNotFound() {
        given(characterDao.findById(FRIEND_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.createFriendRequest(SENDER_ID, FRIEND_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void friendRequestAlreadyExists() {
        given(characterDao.findById(FRIEND_ID)).willReturn(Optional.of(character));
        given(friendRequestDao.findBySenderIdAndFriendId(SENDER_ID, FRIEND_ID)).willReturn(Optional.of(friendRequest));

        Throwable ex = catchThrowable(() -> underTest.createFriendRequest(SENDER_ID, FRIEND_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_ALREADY_EXISTS);
    }

    @Test
    public void friendshipAlreadyExists() {
        given(characterDao.findById(FRIEND_ID)).willReturn(Optional.of(character));
        given(friendRequestDao.findBySenderIdAndFriendId(SENDER_ID, FRIEND_ID)).willReturn(Optional.empty());
        given(friendshipDao.findByFriendIds(SENDER_ID, FRIEND_ID)).willReturn(Optional.of(friendship));

        Throwable ex = catchThrowable(() -> underTest.createFriendRequest(SENDER_ID, FRIEND_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
    }

    @Test
    public void createFriendRequest() {
        given(characterDao.findById(FRIEND_ID)).willReturn(Optional.of(character));
        given(friendRequestDao.findBySenderIdAndFriendId(SENDER_ID, FRIEND_ID)).willReturn(Optional.empty());
        given(friendshipDao.findByFriendIds(SENDER_ID, FRIEND_ID)).willReturn(Optional.empty());
        given(friendRequestFactory.create(SENDER_ID, FRIEND_ID)).willReturn(friendRequest);
        given(friendRequestToResponseConverter.toSentFriendRequest(friendRequest)).willReturn(sentFriendRequestResponse);
        given(friendRequestToResponseConverter.toIncomingFriendRequest(friendRequest)).willReturn(incomingFriendRequestResponse);

        SentFriendRequestResponse result = underTest.createFriendRequest(SENDER_ID, FRIEND_ID);

        verify(friendRequestDao).save(friendRequest);

        then(friendshipWebSocketHandler).should().sendEvent(FRIEND_ID, WebSocketEventName.SKYXPLORE_MAIN_MENU_FRIEND_REQUEST_SENT, incomingFriendRequestResponse);

        assertThat(result).isEqualTo(sentFriendRequestResponse);
    }
}