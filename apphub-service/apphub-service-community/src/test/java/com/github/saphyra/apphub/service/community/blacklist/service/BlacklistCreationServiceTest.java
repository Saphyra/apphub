package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.api.community.model.response.blacklist.BlacklistResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.friendship.dao.friend.FriendshipDao;
import com.github.saphyra.apphub.service.community.friendship.dao.request.FriendRequestDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BlacklistCreationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();

    @Mock
    private AccountClientProxy accountClientProxy;

    @Mock
    private BlacklistDao blacklistDao;

    @Mock
    private BlacklistFactory blacklistFactory;

    @Mock
    private BlacklistToResponseConverter blacklistToResponseConverter;

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private FriendRequestDao friendRequestDao;

    @InjectMocks
    private BlacklistCreationService underTest;

    @Mock
    private Blacklist blacklist;

    @Mock
    private BlacklistResponse blacklistResponse;

    @Test
    public void blacklistAlreadyExists() {
        given(blacklistDao.findByUserIdOrBlockedUserId(USER_ID, BLOCKED_USER_ID)).willReturn(Optional.of(blacklist));

        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, BLOCKED_USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS);
    }

    @Test
    public void userDoesNotExist() {
        given(blacklistDao.findByUserIdOrBlockedUserId(USER_ID, BLOCKED_USER_ID)).willReturn(Optional.empty());
        given(accountClientProxy.userExists(BLOCKED_USER_ID)).willReturn(false);

        Throwable ex = catchThrowable(() -> underTest.create(USER_ID, BLOCKED_USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void create() {
        given(blacklistDao.findByUserIdOrBlockedUserId(USER_ID, BLOCKED_USER_ID)).willReturn(Optional.empty());
        given(accountClientProxy.userExists(BLOCKED_USER_ID)).willReturn(true);
        given(blacklistFactory.create(USER_ID, BLOCKED_USER_ID)).willReturn(blacklist);
        given(blacklistToResponseConverter.convert(blacklist)).willReturn(blacklistResponse);

        BlacklistResponse result = underTest.create(USER_ID, BLOCKED_USER_ID);

        verify(friendshipDao).deleteByUserIdAndFriendId(USER_ID, BLOCKED_USER_ID);
        verify(friendRequestDao).deleteBySenderIdAndReceiverId(USER_ID, BLOCKED_USER_ID);
        verify(blacklistDao).save(blacklist);

        assertThat(result).isEqualTo(blacklistResponse);
    }
}