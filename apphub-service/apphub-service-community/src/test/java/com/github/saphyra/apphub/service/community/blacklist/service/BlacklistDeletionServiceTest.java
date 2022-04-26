package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BlacklistDeletionServiceTest {
    private static final UUID BLACKLIST_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private BlacklistDao blacklistDao;

    @InjectMocks
    private BlacklistDeletionService underTest;

    @Mock
    private Blacklist blacklist;

    @Test
    public void notFound() {
        given(blacklistDao.findById(BLACKLIST_ID)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.delete(USER_ID, BLACKLIST_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    public void forbiddenOperation() {
        given(blacklistDao.findById(BLACKLIST_ID)).willReturn(Optional.of(blacklist));
        given(blacklist.getUserId()).willReturn(UUID.randomUUID());

        Throwable ex = catchThrowable(() -> underTest.delete(USER_ID, BLACKLIST_ID));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void delete() {
        given(blacklistDao.findById(BLACKLIST_ID)).willReturn(Optional.of(blacklist));
        given(blacklist.getUserId()).willReturn(USER_ID);

        underTest.delete(USER_ID, BLACKLIST_ID);

        verify(blacklistDao).delete(blacklist);
    }
}