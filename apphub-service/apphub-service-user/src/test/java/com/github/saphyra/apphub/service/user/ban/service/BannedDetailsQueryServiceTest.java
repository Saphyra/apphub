package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BannedDetailsResponse;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BannedDetailsQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String REQUIRED_ROLE = "required-role";
    private static final String IRRELEVANT_ROLE = "irrelevant-role";
    private static final LocalDateTime EXPIRATION_1 = LocalDateTime.now();
    private static final LocalDateTime EXPIRATION_2 = EXPIRATION_1.plusDays(1);

    @Mock
    private BanDao banDao;

    @InjectMocks
    private BannedDetailsQueryService underTest;

    @Mock
    private Ban irrelevantBan;

    @Mock
    private Ban ban;

    @Test
    void noBans() {
        given(banDao.getByUserId(USER_ID)).willReturn(List.of(irrelevantBan));
        given(irrelevantBan.getBannedRole()).willReturn(IRRELEVANT_ROLE);

        assertThat(underTest.getBannedDetails(USER_ID, List.of(REQUIRED_ROLE)))
            .returns(null, BannedDetailsResponse::getBannedUntil)
            .returns(null, BannedDetailsResponse::getPermanent);
    }

    @Test
    void permanentBan() {
        given(banDao.getByUserId(USER_ID)).willReturn(List.of(ban, irrelevantBan));
        given(irrelevantBan.getBannedRole()).willReturn(IRRELEVANT_ROLE);
        given(ban.getBannedRole()).willReturn(REQUIRED_ROLE);
        given(ban.getPermanent()).willReturn(true);

        assertThat(underTest.getBannedDetails(USER_ID, List.of(REQUIRED_ROLE)))
            .returns(null, BannedDetailsResponse::getBannedUntil)
            .returns(true, BannedDetailsResponse::getPermanent);
    }

    @Test
    void temporaryBan() {
        given(banDao.getByUserId(USER_ID)).willReturn(List.of(ban, ban, irrelevantBan));
        given(irrelevantBan.getBannedRole()).willReturn(IRRELEVANT_ROLE);
        given(ban.getBannedRole()).willReturn(REQUIRED_ROLE);
        given(ban.getPermanent()).willReturn(false);
        given(ban.getExpiration())
            .willReturn(EXPIRATION_1)
            .willReturn(EXPIRATION_2);

        assertThat(underTest.getBannedDetails(USER_ID, List.of(REQUIRED_ROLE)))
            .returns(EXPIRATION_2.toEpochSecond(ZoneOffset.UTC), BannedDetailsResponse::getBannedUntil)
            .returns(false, BannedDetailsResponse::getPermanent);
    }
}