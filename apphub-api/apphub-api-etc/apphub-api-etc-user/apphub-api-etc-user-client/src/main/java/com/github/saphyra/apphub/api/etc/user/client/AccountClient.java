package com.github.saphyra.apphub.api.etc.user.client;

import com.github.saphyra.apphub.api.etc.user.model.UserEndpoints;
import com.github.saphyra.apphub.api.etc.user.model.account.AccountResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.UUID;

public interface AccountClient {
    @GetExchange(UserEndpoints.USER_DATA_INTERNAL_GET_USER_LANGUAGE)
    String getLanguage(@PathVariable("userId") UUID userId);

    @GetExchange(UserEndpoints.USER_DATA_INTERNAL_GET_ACCOUNT)
    AccountResponse getAccountInternal(@PathVariable("userId") UUID userId);

    @PostExchange(UserEndpoints.USER_DATA_SEARCH_ACCOUNT)
    List<AccountResponse> searchAccount(@RequestBody OneParamRequest<String> search, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader);

    @GetExchange(UserEndpoints.USER_DATA_INTERNAL_USER_EXISTS)
    boolean userExists(@PathVariable("userId") UUID userId);
}
