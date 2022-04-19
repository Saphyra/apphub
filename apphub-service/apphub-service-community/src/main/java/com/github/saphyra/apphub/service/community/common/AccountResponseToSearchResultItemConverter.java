package com.github.saphyra.apphub.service.community.common;

import com.github.saphyra.apphub.api.community.model.response.SearchResultItem;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AccountResponseToSearchResultItemConverter {
    public SearchResultItem convert(AccountResponse accountResponse) {
        return SearchResultItem.builder()
            .userId(accountResponse.getUserId())
            .username(accountResponse.getUsername())
            .email(accountResponse.getEmail())
            .build();
    }
}
