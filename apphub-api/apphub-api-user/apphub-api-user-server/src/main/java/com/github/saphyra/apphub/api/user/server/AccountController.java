package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

public interface AccountController {
    @RequestMapping(method = RequestMethod.POST, value = Endpoints.ACCOUNT_CHANGE_EMAIL)
    void changeEmail(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody ChangeEmailRequest request);

    @RequestMapping(method = RequestMethod.POST, value = Endpoints.ACCOUNT_CHANGE_USERNAME)
    void changeUsername(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody ChangeUsernameRequest request);

    @RequestMapping(method = RequestMethod.POST, value = Endpoints.ACCOUNT_CHANGE_PASSWORD)
    void changePassword(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody ChangePasswordRequest request);

    @RequestMapping(method = RequestMethod.DELETE, value = Endpoints.ACCOUNT_DELETE_ACCOUNT)
    void deleteAccount(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody OneParamRequest<String> password);

    @RequestMapping(method = RequestMethod.POST, value = Endpoints.ACCOUNT_REGISTER)
    void register(@RequestBody RegistrationRequest registrationRequest, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(Endpoints.USER_DATA_INTERNAL_USER_GET_USERNAME)
    String getUsernameByUserId(@PathVariable("userId") UUID userId);

    @PostMapping(Endpoints.USER_DATA_SEARCH_ACCOUNT)
    List<AccountResponse> searchAccount(@RequestBody OneParamRequest<String> search, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
