package com.github.saphyra.apphub.service.user.ban.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
public class BanConverterTest {
    private static final UUID BAN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String BANNED_ROLE = "banned-role";
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();
    private static final String REASON = "reason";
    private static final UUID BANNED_BY = UUID.randomUUID();
    private static final String BAN_ID_STRING = "ban-id";
    private static final String USER_ID_STRING = "user-id";
    private static final String BANNED_BY_STRING = "banned-by";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private BanConverter underTest;

    @Test
    public void convertDomain() {
        Ban domain = Ban.builder()
            .id(BAN_ID)
            .userId(USER_ID)
            .bannedRole(BANNED_ROLE)
            .expiration(EXPIRATION)
            .permanent(true)
            .reason(REASON)
            .bannedBy(BANNED_BY)
            .build();

        given(uuidConverter.convertDomain(BAN_ID)).willReturn(BAN_ID_STRING);
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(BANNED_BY)).willReturn(BANNED_BY_STRING);

        BanEntity result = underTest.convertDomain(domain);

        assertThat(result.getId()).isEqualTo(BAN_ID_STRING);
        assertThat(result.getUserId()).isEqualTo(USER_ID_STRING);
        assertThat(result.getBannedRole()).isEqualTo(BANNED_ROLE);
        assertThat(result.getExpiration()).isEqualTo(EXPIRATION);
        assertThat(result.getPermanent()).isTrue();
        assertThat(result.getReason()).isEqualTo(REASON);
        assertThat(result.getBannedBy()).isEqualTo(BANNED_BY_STRING);
    }

    @Test
    public void convertEntity() {
        BanEntity entity = BanEntity.builder()
            .id(BAN_ID_STRING)
            .userId(USER_ID_STRING)
            .bannedRole(BANNED_ROLE)
            .expiration(EXPIRATION)
            .permanent(true)
            .reason(REASON)
            .bannedBy(BANNED_BY_STRING)
            .build();

        given(uuidConverter.convertEntity(BAN_ID_STRING)).willReturn(BAN_ID);
        given(uuidConverter.convertEntity(USER_ID_STRING)).willReturn(USER_ID);
        given(uuidConverter.convertEntity(BANNED_BY_STRING)).willReturn(BANNED_BY);

        Ban result = underTest.convertEntity(entity);

        assertThat(result.getId()).isEqualTo(BAN_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getBannedRole()).isEqualTo(BANNED_ROLE);
        assertThat(result.getExpiration()).isEqualTo(EXPIRATION);
        assertThat(result.getPermanent()).isTrue();
        assertThat(result.getReason()).isEqualTo(REASON);
        assertThat(result.getBannedBy()).isEqualTo(BANNED_BY);
    }
}