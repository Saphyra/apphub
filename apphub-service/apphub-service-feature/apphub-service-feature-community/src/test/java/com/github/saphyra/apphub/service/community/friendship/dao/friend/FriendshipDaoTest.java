package com.github.saphyra.apphub.service.community.friendship.dao.friend;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FriendshipDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID FRIEND_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String FRIEND_ID_STRING = "friend-id";
    private static final UUID FRIENDSHIP_ID = UUID.randomUUID();
    private static final String FRIENDSHIP_ID_STRING = "friendship-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private FriendshipRepository repository;

    @Mock
    private FriendshipConverter converter;

    @InjectMocks
    private FriendshipDao underTest;

    @Mock
    private FriendshipEntity entity;

    @Mock
    private Friendship domain;

    @Test
    public void deleteByUserIdAndFriendId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(FRIEND_ID)).willReturn(FRIEND_ID_STRING);

        underTest.deleteByUserIdAndFriendId(USER_ID, FRIEND_ID);

        verify(repository).deleteByUserIdAndFriendId(USER_ID_STRING, FRIEND_ID_STRING);
    }

    @Test
    public void getByUserIdAndFriendId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserIdOrFriendId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        List<Friendship> result = underTest.getByUserIdOrFriendId(USER_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findByUserIdAndFriendId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(FRIEND_ID)).willReturn(FRIEND_ID_STRING);
        given(repository.findByUserIdAndFriendId(USER_ID_STRING, FRIEND_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<Friendship> result = underTest.findByUserIdAndFriendId(USER_ID, FRIEND_ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(FRIENDSHIP_ID)).willReturn(FRIENDSHIP_ID_STRING);
        given(repository.findById(FRIENDSHIP_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<Friendship> result = underTest.findById(FRIENDSHIP_ID);

        assertThat(result).contains(domain);
    }

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }
}