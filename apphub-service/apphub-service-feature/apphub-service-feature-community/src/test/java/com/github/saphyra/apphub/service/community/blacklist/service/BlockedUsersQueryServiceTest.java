package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BlockedUsersQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();

    @Mock
    private BlacklistDao blacklistDao;

    @InjectMocks
    private BlockedUsersQueryService underTest;

    @Mock
    private Blacklist blacklist;

    @Test
    public void getUserIdsCannotContactWith() {
        given(blacklistDao.getByUserIdOrBlockedUserId(USER_ID)).willReturn(List.of(blacklist));
        given(blacklist.getOtherUserId(USER_ID)).willReturn(BLOCKED_USER_ID);

        List<UUID> result = underTest.getUserIdsCannotContactWith(USER_ID);

        assertThat(result).containsExactly(BLOCKED_USER_ID);
    }
}