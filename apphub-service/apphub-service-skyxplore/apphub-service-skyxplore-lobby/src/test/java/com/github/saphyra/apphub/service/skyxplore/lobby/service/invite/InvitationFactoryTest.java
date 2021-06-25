package com.github.saphyra.apphub.service.skyxplore.lobby.service.invite;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
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
public class InvitationFactoryTest {
    private static final LocalDateTime CURRENT_DATE = LocalDateTime.now();
    private static final UUID CHARACTER_ID = UUID.randomUUID();
    private static final UUID INVITOR_ID = UUID.randomUUID();

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private InvitationFactory underTest;

    @Test
    public void create() {
        given(dateTimeUtil.getCurrentDate()).willReturn(CURRENT_DATE);

        Invitation result = underTest.create(INVITOR_ID, CHARACTER_ID);

        assertThat(result.getInvitorId()).isEqualTo(INVITOR_ID);
        assertThat(result.getCharacterId()).isEqualTo(CHARACTER_ID);
        assertThat(result.getInvitationTime()).isEqualTo(CURRENT_DATE);
    }
}