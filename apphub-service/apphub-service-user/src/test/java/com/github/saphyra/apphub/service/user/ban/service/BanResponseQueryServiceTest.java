package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.response.BanDetailsResponse;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BanResponseQueryServiceTest {
    private static final UUID BANNED_USER_ID = UUID.randomUUID();
    private static final UUID BANNED_BY_ID = UUID.randomUUID();
    private static final UUID BAN_ID = UUID.randomUUID();
    private static final String BANNED_ROLE = "banned-role";
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();
    private static final String REASON = "reason";
    private static final String BANNED_USER_EMAIL = "banned-user-email";
    private static final String BANNED_USER_USERNAME = "banned-user-username";
    private static final String BANNED_BY_USER_EMAIL = "banned-by-user-email";
    private static final String BANNED_BY_USER_USERNAME = "banned-by-user-username";
    private static final LocalDateTime MARKED_FOR_DELETION_AT = EXPIRATION.plusDays(1);

    @Mock
    private BanDao banDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private BanResponseQueryService underTest;

    @Mock
    private User bannedUser;

    @Mock
    private User bannedByUser;

    @Test
    public void getBans() {
        Ban ban = Ban.builder()
            .id(BAN_ID)
            .userId(BANNED_USER_ID)
            .bannedRole(BANNED_ROLE)
            .expiration(EXPIRATION)
            .permanent(true)
            .reason(REASON)
            .bannedBy(BANNED_BY_ID)
            .build();
        given(banDao.getByUserId(BANNED_USER_ID)).willReturn(List.of(ban));
        given(userDao.findByIdValidated(BANNED_USER_ID)).willReturn(bannedUser);
        given(userDao.findByIdValidated(BANNED_BY_ID)).willReturn(bannedByUser);
        given(bannedUser.getEmail()).willReturn(BANNED_USER_EMAIL);
        given(bannedUser.getUsername()).willReturn(BANNED_USER_USERNAME);
        given(bannedByUser.getEmail()).willReturn(BANNED_BY_USER_EMAIL);
        given(bannedByUser.getUsername()).willReturn(BANNED_BY_USER_USERNAME);
        given(bannedUser.isMarkedForDeletion()).willReturn(true);
        given(bannedUser.getMarkedForDeletionAt()).willReturn(MARKED_FOR_DELETION_AT);

        BanResponse result = underTest.getBans(BANNED_USER_ID);

        assertThat(result.getUserId()).isEqualTo(BANNED_USER_ID);
        assertThat(result.getUsername()).isEqualTo(BANNED_USER_USERNAME);
        assertThat(result.getEmail()).isEqualTo(BANNED_USER_EMAIL);
        assertThat(result.getMarkedForDeletion()).isTrue();
        assertThat(result.getMarkedForDeletionAt()).isEqualTo(MARKED_FOR_DELETION_AT.toEpochSecond(ZoneOffset.UTC));

        assertThat(result.getBans()).hasSize(1);
        BanDetailsResponse response = result.getBans().get(0);
        assertThat(response.getId()).isEqualTo(BAN_ID);
        assertThat(response.getBannedRole()).isEqualTo(BANNED_ROLE);
        assertThat(response.getExpiration()).isEqualTo(EXPIRATION.toEpochSecond(ZoneOffset.UTC));
        assertThat(response.getPermanent()).isTrue();
        assertThat(response.getReason()).isEqualTo(REASON);
        assertThat(response.getBannedById()).isEqualTo(BANNED_BY_ID);
        assertThat(response.getBannedByUsername()).isEqualTo(BANNED_BY_USER_USERNAME);
        assertThat(response.getBannedByEmail()).isEqualTo(BANNED_BY_USER_EMAIL);
    }

    @Test
    public void getBans_nullMarkedForDeletionAt() {
        Ban ban = Ban.builder()
            .id(BAN_ID)
            .userId(BANNED_USER_ID)
            .bannedRole(BANNED_ROLE)
            .expiration(EXPIRATION)
            .permanent(true)
            .reason(REASON)
            .bannedBy(BANNED_BY_ID)
            .build();
        given(banDao.getByUserId(BANNED_USER_ID)).willReturn(List.of(ban));
        given(userDao.findByIdValidated(BANNED_USER_ID)).willReturn(bannedUser);
        given(userDao.findByIdValidated(BANNED_BY_ID)).willReturn(bannedByUser);
        given(bannedUser.getEmail()).willReturn(BANNED_USER_EMAIL);
        given(bannedUser.getUsername()).willReturn(BANNED_USER_USERNAME);
        given(bannedByUser.getEmail()).willReturn(BANNED_BY_USER_EMAIL);
        given(bannedByUser.getUsername()).willReturn(BANNED_BY_USER_USERNAME);
        given(bannedUser.isMarkedForDeletion()).willReturn(true);
        given(bannedUser.getMarkedForDeletionAt()).willReturn(null);

        BanResponse result = underTest.getBans(BANNED_USER_ID);

        assertThat(result.getUserId()).isEqualTo(BANNED_USER_ID);
        assertThat(result.getUsername()).isEqualTo(BANNED_USER_USERNAME);
        assertThat(result.getEmail()).isEqualTo(BANNED_USER_EMAIL);
        assertThat(result.getMarkedForDeletion()).isTrue();
        assertThat(result.getMarkedForDeletionAt()).isNull();

        assertThat(result.getBans()).hasSize(1);
        BanDetailsResponse response = result.getBans().get(0);
        assertThat(response.getId()).isEqualTo(BAN_ID);
        assertThat(response.getBannedRole()).isEqualTo(BANNED_ROLE);
        assertThat(response.getExpiration()).isEqualTo(EXPIRATION.toEpochSecond(ZoneOffset.UTC));
        assertThat(response.getPermanent()).isTrue();
        assertThat(response.getReason()).isEqualTo(REASON);
        assertThat(response.getBannedById()).isEqualTo(BANNED_BY_ID);
        assertThat(response.getBannedByUsername()).isEqualTo(BANNED_BY_USER_USERNAME);
        assertThat(response.getBannedByEmail()).isEqualTo(BANNED_BY_USER_EMAIL);
    }
}