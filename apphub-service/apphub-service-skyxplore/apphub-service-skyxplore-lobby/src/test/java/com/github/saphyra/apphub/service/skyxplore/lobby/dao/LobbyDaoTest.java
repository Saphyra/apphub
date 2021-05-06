package com.github.saphyra.apphub.service.skyxplore.lobby.dao;

import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class LobbyDaoTest {
    private static final UUID LOBBY_ID_1 = UUID.randomUUID();
    private static final UUID LOBBY_ID_2 = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();

    @InjectMocks
    private LobbyDao underTest;

    @Mock
    private Lobby lobby1;

    @Mock
    private Lobby lobby2;

    @Mock
    private Member member;

    @Before
    public void setUp() {
        given(lobby1.getLobbyId()).willReturn(LOBBY_ID_1);
        given(lobby2.getLobbyId()).willReturn(LOBBY_ID_2);

        underTest.save(lobby1);
        underTest.save(lobby2);
    }

    @Test
    public void findByUserId() {
        given(lobby1.getMembers()).willReturn(CollectionUtils.singleValueMap(USER_ID, member));

        Optional<Lobby> result = underTest.findByUserId(USER_ID);

        assertThat(result).contains(lobby1);
    }

    @Test
    public void findByUserIdValidated_notFound() {
        Throwable ex = catchThrowable(() -> underTest.findByUserIdValidated(USER_ID));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.LOBBY_NOT_FOUND.name());
    }

    @Test
    public void getByLastAccessedBefore() {
        given(lobby1.getLastAccess()).willReturn(CURRENT_DATE.minusSeconds(1));
        given(lobby2.getLastAccess()).willReturn(CURRENT_DATE);

        List<Lobby> result = underTest.getByLastAccessedBefore(CURRENT_DATE);

        assertThat(result).containsExactly(lobby1);
    }

    @Test
    public void delete() {
        underTest.delete(lobby1);

        assertThat(underTest.getAll()).containsExactly(lobby2);
    }
}