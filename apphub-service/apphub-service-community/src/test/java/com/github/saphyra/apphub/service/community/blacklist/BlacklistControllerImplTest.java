package com.github.saphyra.apphub.service.community.blacklist;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.community.model.response.blacklist.BlacklistResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.community.blacklist.service.BlacklistCreationService;
import com.github.saphyra.apphub.service.community.blacklist.service.BlacklistDeletionService;
import com.github.saphyra.apphub.service.community.blacklist.service.BlacklistQueryService;
import com.github.saphyra.apphub.service.community.blacklist.service.BlacklistSearchService;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BlacklistControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String QUERY = "query";
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();
    private static final UUID BLACKLIST_ID = UUID.randomUUID();

    @Mock
    private BlacklistSearchService blacklistSearchService;

    @Mock
    private BlacklistQueryService blacklistQueryService;

    @Mock
    private BlacklistCreationService blacklistCreationService;

    @Mock
    private BlacklistDeletionService blacklistDeletionService;

    @InjectMocks
    private BlacklistControllerImpl underTest;

    @Mock
    private SearchResultItem searchResultItem;

    @Mock
    private BlacklistResponse blacklistResponse;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void search() {
        given(blacklistSearchService.search(USER_ID, QUERY)).willReturn(List.of(searchResultItem));

        List<SearchResultItem> result = underTest.search(new OneParamRequest<>(QUERY), accessTokenHeader);

        assertThat(result).containsExactly(searchResultItem);
    }

    @Test
    public void getBlacklist() {
        given(blacklistQueryService.getBlacklist(USER_ID)).willReturn(List.of(blacklistResponse));

        List<BlacklistResponse> result = underTest.getBlacklist(accessTokenHeader);

        assertThat(result).containsExactly(blacklistResponse);
    }

    @Test
    public void create() {
        given(blacklistCreationService.create(USER_ID, BLOCKED_USER_ID)).willReturn(blacklistResponse);

        BlacklistResponse result = underTest.create(new OneParamRequest<>(BLOCKED_USER_ID), accessTokenHeader);

        assertThat(result).isEqualTo(blacklistResponse);
    }

    @Test
    public void delete() {
        underTest.delete(BLACKLIST_ID, accessTokenHeader);

        verify(blacklistDeletionService).delete(USER_ID, BLACKLIST_ID);
    }
}