package com.github.saphyra.apphub.api.user.server;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface AccountController {
    @PostMapping(Endpoints.ACCOUNT_CHANGE_EMAIL)
    void changeEmail(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody ChangeEmailRequest request);

    @PostMapping(Endpoints.ACCOUNT_CHANGE_USERNAME)
    void changeUsername(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody ChangeUsernameRequest request);

    @PostMapping(Endpoints.ACCOUNT_CHANGE_PASSWORD)
    void changePassword(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody ChangePasswordRequest request);

    @DeleteMapping(Endpoints.ACCOUNT_DELETE_ACCOUNT)
    void deleteAccount(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader, @RequestBody OneParamRequest<String> password);

    @PostMapping(Endpoints.ACCOUNT_REGISTER)
    void register(@RequestBody RegistrationRequest registrationRequest, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @GetMapping(Endpoints.USER_DATA_GET_USERNAME)
    OneParamResponse<String> getUsernameByUserId(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    /**
     * Searching for users having their email or username contains the query string, filtering own account, and accounts marked for deletion based on query parameter
     */
    @PostMapping(Endpoints.USER_DATA_SEARCH_ACCOUNT)
    List<AccountResponse> searchAccount(
        @RequestBody OneParamRequest<String> search,
        @RequestParam(value = "includeMarkedForDeletion", required = false, defaultValue = "false") Boolean includeMarkedForDeletion,
        @RequestParam(value = "includeSelf", required = false, defaultValue = "false") Boolean includeSelf,
        @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader
    );

    @GetMapping(Endpoints.USER_DATA_INTERNAL_GET_ACCOUNT)
    AccountResponse getAccount(@PathVariable("userId") UUID userId);

    @GetMapping(Endpoints.USER_DATA_INTERNAL_USER_EXISTS)
    boolean userExists(@PathVariable("userId") UUID userId);
}
