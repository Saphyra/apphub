package com.github.saphyra.apphub.service.community.blacklist.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BlacklistDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();
    private static final String BLOCKED_USER_ID_STRING = "blocked-user-id";
    private static final UUID BLACKLIST_ID = UUID.randomUUID();
    private static final String BLACKLIST_ID_STRING = "blacklist-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BlacklistRepository repository;

    @Mock
    private BlacklistConverter converter;

    @InjectMocks
    private BlacklistDao underTest;

    @Mock
    private BlacklistEntity entity;

    @Mock
    private Blacklist blacklist;

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(blacklist));

        List<Blacklist> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(blacklist);
    }

    @Test
    public void findByUserIdOrBlockedUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(BLOCKED_USER_ID)).willReturn(BLOCKED_USER_ID_STRING);
        given(repository.findByUserIdOrBlockedUserId(USER_ID_STRING, BLOCKED_USER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(blacklist));

        Optional<Blacklist> result = underTest.findByUserIdOrBlockedUserId(USER_ID, BLOCKED_USER_ID);

        assertThat(result).contains(blacklist);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(BLACKLIST_ID)).willReturn(BLACKLIST_ID_STRING);
        given(repository.findById(BLACKLIST_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(blacklist));

        Optional<Blacklist> result = underTest.findById(BLACKLIST_ID);

        assertThat(result).contains(blacklist);
    }

    @Test
    public void getByUserIdOrBlockedUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserIdOrBlockedUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(blacklist));

        List<Blacklist> result = underTest.getByUserIdOrBlockedUserId(USER_ID);

        assertThat(result).containsExactly(blacklist);
    }

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }
}