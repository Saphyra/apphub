package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BanServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";

    @Mock
    private BanRequestValidator banRequestValidator;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private BanFactory banFactory;

    @Mock
    private BanDao banDao;

    @InjectMocks
    private BanService underTest;

    @Mock
    private BanRequest request;

    @Mock
    private Ban ban;

    @Test
    public void ban() {
        given(request.getPassword()).willReturn(PASSWORD);
        given(banFactory.create(USER_ID, request)).willReturn(ban);

        underTest.ban(USER_ID, request);

        verify(banRequestValidator).validate(request);
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
        verify(banDao).save(ban);
    }
}