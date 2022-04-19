package com.github.saphyra.apphub.service.community.common;

import com.github.saphyra.apphub.api.user.client.AccountClient;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AccountClientProxy {
    private final LocaleProvider localeProvider;
    private final AccountClient accountClient;
    private final AccessTokenProvider accessTokenProvider;

    public List<AccountResponse> search(String query) {
        return accountClient.searchAccount(new OneParamRequest<>(query), accessTokenProvider.getAsString(), localeProvider.getLocaleValidated());
    }

    public AccountResponse getAccount(UUID userId) {
        return accountClient.getAccount(userId, localeProvider.getLocaleValidated());
    }

    public boolean userExists(UUID userId) {
        return accountClient.userExists(userId, localeProvider.getOrDefault());
    }
}
