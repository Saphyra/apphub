package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import com.github.saphyra.apphub.service.community.common.AccountResponseToSearchResultItemConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BlacklistSearchService {
    private final BlacklistDao blacklistDao;
    private final AccountClientProxy accountClientProxy;
    private final AccountResponseToSearchResultItemConverter accountResponseToSearchResultItemConverter;

    public List<SearchResultItem> search(UUID userId, String queryString) {
        List<UUID> blockedUsers = blacklistDao.getByUserId(userId)
            .stream()
            .map(Blacklist::getBlockedUserId)
            .collect(Collectors.toList());

        return accountClientProxy.search(queryString)
            .stream()
            .filter(accountResponse -> !blockedUsers.contains(accountResponse.getUserId()))
            .map(accountResponseToSearchResultItemConverter::convert)
            .collect(Collectors.toList());
    }
}
