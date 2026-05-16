package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.etc.user.model.ban.BanSearchResponse;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.CustomAssertions;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BanSearchServiceTest {
    private static final String QUERY = "query";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";

    @Mock
    private UserDao userDao;

    @Mock
    private BanDao banDao;

    @InjectMocks
    private BanSearchService underTest;

    @Mock
    private User user;

    @Mock
    private Ban ban;

    @Test
    void queryTooShort() {
        Throwable ex = catchThrowable(() -> underTest.search("as"));

        ExceptionValidator.validateInvalidParam(ex, "query", "too short");
    }

    @Test
    void search() {
        given(userDao.findByUserIdentifier(QUERY)).willReturn(Optional.of(user));
        given(user.getUserId()).willReturn(USER_ID);
        given(user.getUsername()).willReturn(USERNAME);
        given(user.getEmail()).willReturn(EMAIL);
        given(user.isMarkedForDeletion()).willReturn(true);
        given(banDao.getByUserId(USER_ID)).willReturn(List.of(ban));
        given(ban.getBannedRole()).willReturn(Role.TEST);

        List<BanSearchResponse> result = underTest.search(QUERY);

        CustomAssertions.singleListAssertThat(result)
            .returns(USER_ID, BanSearchResponse::getUserId)
            .returns(USERNAME, BanSearchResponse::getUsername)
            .returns(EMAIL, BanSearchResponse::getEmail)
            .returns(true, BanSearchResponse::getMarkedForDeletion)
            .returns(List.of(Role.TEST), BanSearchResponse::getBannedRoles);
    }
}