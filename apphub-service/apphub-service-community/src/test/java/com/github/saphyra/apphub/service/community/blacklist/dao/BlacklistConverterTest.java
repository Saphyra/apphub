package com.github.saphyra.apphub.service.community.blacklist.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BlacklistConverterTest {
    private static final UUID BLACKLIST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();
    private static final String BLACKLIST_ID_STRING = "blacklist-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String BLOCKED_USER_ID_STRING = "blocked-user-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BlacklistConverter underTest;

    @Test
    public void convertDomain() {
        Blacklist blacklist = Blacklist.builder()
            .blacklistId(BLACKLIST_ID)
            .userId(USER_ID)
            .blockedUserId(BLOCKED_USER_ID)
            .build();

        given(uuidConverter.convertDomain(BLACKLIST_ID)).willReturn(BLACKLIST_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(BLOCKED_USER_ID)).willReturn(BLOCKED_USER_ID_STRING);

        BlacklistEntity result = underTest.convertDomain(blacklist);

        assertThat(result.getBlacklistId()).isEqualTo(BLACKLIST_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getBlockedUserId()).isEqualTo(BLOCKED_USER_ID_STRING);
    }

    @Test
    public void convertEntity() {
        BlacklistEntity blacklistEntity = BlacklistEntity.builder()
            .blacklistId(BLACKLIST_ID_STRING)
            .userId(USER_ID_STRING)
            .blockedUserId(BLOCKED_USER_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(BLACKLIST_ID_STRING)).willReturn(BLACKLIST_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(BLOCKED_USER_ID_STRING)).willReturn(BLOCKED_USER_ID);

        Blacklist result = underTest.convertEntity(blacklistEntity);

        assertThat(result.getBlacklistId()).isEqualTo(BLACKLIST_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getBlockedUserId()).isEqualTo(BLOCKED_USER_ID);
    }
}