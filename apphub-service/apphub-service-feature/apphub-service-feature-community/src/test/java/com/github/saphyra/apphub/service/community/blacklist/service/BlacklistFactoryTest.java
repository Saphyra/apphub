package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BlacklistFactoryTest {
    private static final UUID BLACKLIST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private BlacklistFactory underTest;


    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(BLACKLIST_ID);

        Blacklist result = underTest.create(USER_ID, BLOCKED_USER_ID);

        assertThat(result.getBlacklistId()).isEqualTo(BLACKLIST_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getBlockedUserId()).isEqualTo(BLOCKED_USER_ID);
    }
}