package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BanResponse;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UnmarkUserForDeletionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private UserDao userDao;

    @Mock
    private BanResponseQueryService banResponseQueryService;

    @InjectMocks
    private UnmarkUserForDeletionService underTest;

    @Mock
    private User user;

    @Mock
    private BanResponse banResponse;

    @Test
    public void unmarkUserForDeletion() {
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);
        given(banResponseQueryService.getBans(USER_ID)).willReturn(banResponse);

        BanResponse result = underTest.unmarkUserForDeletion(USER_ID);

        verify(user).setMarkedForDeletion(false);
        verify(user).setMarkedForDeletionAt(null);

        verify(userDao).save(user);

        assertThat(result).isEqualTo(banResponse);
    }
}