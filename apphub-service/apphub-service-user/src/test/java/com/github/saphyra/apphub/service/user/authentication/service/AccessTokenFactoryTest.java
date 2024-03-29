package com.github.saphyra.apphub.service.user.authentication.service;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.user.authentication.dao.AccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AccessTokenFactoryTest {
    private static final UUID ACCESS_TOKEN_ID = UUID.randomUUID();
    private static final LocalDateTime LAST_ACCESS = LocalDateTime.now();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private AccessTokenFactory underTest;

    @Test
    public void create() {
        given(idGenerator.randomUuid()).willReturn(ACCESS_TOKEN_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(LAST_ACCESS);

        AccessToken result = underTest.create(USER_ID, true);

        assertThat(result.getAccessTokenId()).isEqualTo(ACCESS_TOKEN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getLastAccess()).isEqualTo(LAST_ACCESS);
        assertThat(result.isPersistent()).isTrue();
    }
}