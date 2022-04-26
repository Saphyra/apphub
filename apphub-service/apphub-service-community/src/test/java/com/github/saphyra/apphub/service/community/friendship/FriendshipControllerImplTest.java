package com.github.saphyra.apphub.service.community.friendship;

import com.github.saphyra.apphub.api.community.model.response.friendship.FriendshipResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.service.community.friendship.service.FriendshipDeletionService;
import com.github.saphyra.apphub.service.community.friendship.service.FriendshipQueryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FriendshipControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();

    @Mock
    private FriendshipQueryService friendshipQueryService;

    @Mock
    private FriendshipDeletionService friendshipDeletionService;

    @InjectMocks
    private FriendshipControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private FriendshipResponse friendshipResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getFriendships() {
        given(friendshipQueryService.getFriendships(USER_ID)).willReturn(List.of(friendshipResponse));

        List<FriendshipResponse> result = underTest.getFriendships(accessTokenHeader);

        assertThat(result).containsExactly(friendshipResponse);
    }

    @Test
    public void delete() {
        underTest.delete(FRIENDSHIP_ID, accessTokenHeader);

        verify(friendshipDeletionService).delete(USER_ID, FRIENDSHIP_ID);
    }
}