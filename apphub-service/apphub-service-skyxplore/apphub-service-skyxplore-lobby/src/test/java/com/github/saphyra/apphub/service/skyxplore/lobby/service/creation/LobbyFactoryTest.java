package com.github.saphyra.apphub.service.skyxplore.lobby.service.creation;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LobbyFactoryTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID LOBBY_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOBBY_NAME = "lobby-name";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private LobbyFactory underTest;

    @Test
    public void create() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);
        given(idGenerator.randomUuid()).willReturn(LOBBY_ID);

        Lobby result = underTest.create(USER_ID, LOBBY_NAME);

        assertThat(result.getLobbyId()).isEqualTo(LOBBY_ID);
        assertThat(result.getLobbyName()).isEqualTo(LOBBY_NAME);
        assertThat(result.getHost()).isEqualTo(USER_ID);
        assertThat(result.getMembers()).containsEntry(USER_ID, Member.builder().userId(USER_ID).build());
        assertThat(result.getLastAccess()).isEqualTo(CURRENT_DATE);
    }
}