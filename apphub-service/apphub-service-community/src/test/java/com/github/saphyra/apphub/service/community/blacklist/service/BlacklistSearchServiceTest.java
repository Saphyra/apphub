package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.user.model.account.AccountResponse;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.common.AccountResponseToSearchResultItemConverter;
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
public class BlacklistSearchServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BLOCKED_USER_ID = UUID.randomUUID();
    private static final String QUERY = "query";

    @Mock
    private BlockedUsersQueryService blockedUsersQueryService;

    @Mock
    private AccountClientProxy accountClientProxy;

    @Mock
    private AccountResponseToSearchResultItemConverter accountResponseToSearchResultItemConverter;

    @InjectMocks
    private BlacklistSearchService underTest;

    @Mock
    private AccountResponse filteredAccountResponse;

    @Mock
    private AccountResponse accountResponse;

    @Mock
    private SearchResultItem searchResultItem;

    @Test
    public void search() {
        given(blockedUsersQueryService.getUserIdsCannotContactWith(USER_ID)).willReturn(List.of(BLOCKED_USER_ID));
        given(accountClientProxy.search(QUERY)).willReturn(List.of(filteredAccountResponse, accountResponse));
        given(filteredAccountResponse.getUserId()).willReturn(BLOCKED_USER_ID);
        given(accountResponse.getUserId()).willReturn(UUID.randomUUID());
        given(accountResponseToSearchResultItemConverter.convert(accountResponse)).willReturn(searchResultItem);

        List<SearchResultItem> result = underTest.search(USER_ID, QUERY);

        assertThat(result).containsExactly(searchResultItem);
    }
}