package com.github.saphyra.apphub.service.skyxplore.data.friend;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.CharacterDao;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import com.github.saphyra.apphub.service.skyxplore.data.common.SkyXploreCharacterModelConverter;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.Friendship;
import com.github.saphyra.apphub.service.skyxplore.data.friend.friendship.dao.FriendshipDao;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequest;
import com.github.saphyra.apphub.service.skyxplore.data.friend.request.dao.FriendRequestDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class FriendCandidateQueryServiceTest {
    private static final String QUERY_STRING = "query-string";
    private static final UUID USER_ID_1 = UUID.randomUUID();
    private static final UUID USER_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID_3 = UUID.randomUUID();
    private static final UUID USER_ID_4 = UUID.randomUUID();

    @Mock
    private CharacterDao characterDao;

    @Mock
    private SkyXploreCharacterModelConverter characterModelConverter;

    @Mock
    private FriendRequestDao friendRequestDao;

    @Mock
    private FriendshipDao friendshipDao;

    @InjectMocks
    private FriendCandidateQueryService underTest;

    @Mock
    private SkyXploreCharacter ownCharacter;

    @Mock
    private SkyXploreCharacter friendRequestSent;

    @Mock
    private SkyXploreCharacter alreadyFriend;

    @Mock
    private SkyXploreCharacter friendCandidate;

    @Mock
    private FriendRequest friendRequest;

    @Mock
    private Friendship friendship;

    @Mock
    private SkyXploreCharacterModel model;

    @Test
    public void getFriendCandidates() {
        given(characterDao.getByNameLike(QUERY_STRING)).willReturn(Arrays.asList(ownCharacter, friendRequestSent, alreadyFriend, friendCandidate));
        given(ownCharacter.getUserId()).willReturn(USER_ID_1);
        given(friendRequestSent.getUserId()).willReturn(USER_ID_2);
        given(alreadyFriend.getUserId()).willReturn(USER_ID_3);
        given(friendCandidate.getUserId()).willReturn(USER_ID_4);
        given(friendRequestDao.findBySenderIdAndFriendId(USER_ID_1, USER_ID_2)).willReturn(Optional.of(friendRequest));
        given(friendshipDao.findByFriendIds(USER_ID_1, USER_ID_3)).willReturn(Optional.of(friendship));
        given(characterModelConverter.convertEntity(friendCandidate)).willReturn(model);

        List<SkyXploreCharacterModel> result = underTest.getFriendCandidates(USER_ID_1, QUERY_STRING);

        assertThat(result).containsExactly(model);
    }
}