package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.server.AccountController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeEmailService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangePasswordService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeUsernameService;
import com.github.saphyra.apphub.service.user.data.service.account.DeleteAccountService;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
class AccountControllerImpl implements AccountController {
    private final ChangeEmailService changeEmailService;
    private final ChangePasswordService changePasswordService;
    private final ChangeUsernameService changeUsernameService;
    private final DeleteAccountService deleteAccountService;
    private final RegistrationService registrationService;

    @Override
    public void changeEmail(AccessTokenHeader accessTokenHeader, ChangeEmailRequest request) {
        log.info("{} wants to change his email", accessTokenHeader.getUserId());
        changeEmailService.changeEmail(accessTokenHeader.getUserId(), request);
    }

    @Override
    //TODO api test
    //TODO fe test
    public void changeUsername(AccessTokenHeader accessTokenHeader, ChangeUsernameRequest request) {
        log.info("{} wants to change his username", accessTokenHeader.getUserId());
        changeUsernameService.changeUsername(accessTokenHeader.getUserId(), request);
    }

    @Override
    //TODO api test
    //TODO fe test
    public void changePassword(AccessTokenHeader accessTokenHeader, ChangePasswordRequest request) {
        log.info("{} wants to change his password", accessTokenHeader.getUserId());
        changePasswordService.changePassword(accessTokenHeader.getUserId(), request);
    }

    @Override
    //TODO api test
    //TODO fe test
    public void deleteAccount(AccessTokenHeader accessTokenHeader, OneParamRequest<String> password) {
        log.info("{} wants to delete his account", accessTokenHeader.getUserId());
        deleteAccountService.deleteAccount(accessTokenHeader.getUserId(), password.getValue());
    }

    @Override
    public void register(RegistrationRequest registrationRequest, String locale) {
        log.info("RegistrationRequest arrived for username {} and email {}", registrationRequest.getUsername(), registrationRequest.getEmail());
        registrationService.register(registrationRequest, locale);
    }
}
