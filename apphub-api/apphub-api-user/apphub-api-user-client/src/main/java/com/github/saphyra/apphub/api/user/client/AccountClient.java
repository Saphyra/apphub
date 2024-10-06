package com.github.saphyra.apphub.api.user.client;

import com.github.saphyra.apphub.api.user.model.account.AccountResponse;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.endpoints.UserEndpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-account", url = "${serviceUrls.user}")
public interface AccountClient {
    @RequestMapping(method = RequestMethod.GET, value = UserEndpoints.USER_DATA_INTERNAL_GET_USER_LANGUAGE)
    String getLanguage(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(UserEndpoints.USER_DATA_INTERNAL_GET_ACCOUNT)
    AccountResponse getAccountInternal(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(UserEndpoints.USER_DATA_SEARCH_ACCOUNT)
    List<AccountResponse> searchAccount(@RequestBody OneParamRequest<String> search, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(UserEndpoints.USER_DATA_INTERNAL_USER_EXISTS)
    boolean userExists(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
