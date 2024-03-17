package com.github.saphyra.apphub.service.user.ban;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.api.user.model.request.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.api.user.model.response.BanSearchResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.ban.service.BanResponseQueryService;
import com.github.saphyra.apphub.service.user.ban.service.BanSearchService;
import com.github.saphyra.apphub.service.user.ban.service.BanService;
import com.github.saphyra.apphub.service.user.ban.service.MarkUserForDeletionService;
import com.github.saphyra.apphub.service.user.ban.service.RevokeBanService;
import com.github.saphyra.apphub.service.user.ban.service.UnmarkUserForDeletionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BanControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final UUID BAN_ID = UUID.randomUUID();
    private static final UUID BANNED_USER_ID = UUID.randomUUID();
    private static final UUID DELETED_USER_ID = UUID.randomUUID();
    private static final String QUERY = "query";

    @Mock
    private BanService banService;

    @Mock
    private RevokeBanService revokeBanService;

    @Mock
    private BanResponseQueryService banResponseQueryService;

    @Mock
    private MarkUserForDeletionService markUserForDeletionService;

    @Mock
    private UnmarkUserForDeletionService unmarkUserForDeletionService;

    @Mock
    private BanSearchService banSearchService;

    @InjectMocks
    private BanControllerImpl underTest;

    @Mock
    private BanRequest request;

    @Mock
    private BanResponse response;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private BanResponse banResponse;

    @Mock
    private MarkUserForDeletionRequest markUserForDeletionRequest;

    @Mock
    private BanSearchResponse banSearchResponse;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void banUser() {
        given(banService.ban(USER_ID, request)).willReturn(banResponse);

        assertThat(underTest.banUser(request, accessTokenHeader)).isEqualTo(banResponse);
    }

    @Test
    public void revokeBan() {
        given(revokeBanService.revokeBan(USER_ID, PASSWORD, BAN_ID)).willReturn(banResponse);

        assertThat(underTest.revokeBan(new OneParamRequest<>(PASSWORD), BAN_ID, accessTokenHeader)).isEqualTo(banResponse);
    }

    @Test
    public void getBans() {
        given(banResponseQueryService.getBans(BANNED_USER_ID)).willReturn(response);

        BanResponse result = underTest.getBans(BANNED_USER_ID, accessTokenHeader);

        assertThat(result).isEqualTo(response);
    }

    @Test
    public void markUserForDeletion() {
        given(markUserForDeletionService.markUserForDeletion(DELETED_USER_ID, markUserForDeletionRequest, USER_ID)).willReturn(banResponse);

        BanResponse result = underTest.markUserForDeletion(markUserForDeletionRequest, DELETED_USER_ID, accessTokenHeader);

        assertThat(result).isEqualTo(banResponse);
    }

    @Test
    public void unmarkUserForDeletion() {
        given(unmarkUserForDeletionService.unmarkUserForDeletion(DELETED_USER_ID)).willReturn(banResponse);

        BanResponse result = underTest.unmarkUserForDeletion(DELETED_USER_ID, accessTokenHeader);

        assertThat(result).isEqualTo(banResponse);
    }

    @Test
    void search() {
        given(banSearchService.search(QUERY)).willReturn(List.of(banSearchResponse));

        assertThat(underTest.search(new OneParamRequest<>(QUERY), accessTokenHeader)).containsExactly(banSearchResponse);
    }
}