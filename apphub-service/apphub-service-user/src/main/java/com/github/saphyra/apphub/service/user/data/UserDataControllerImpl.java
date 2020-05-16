package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.model.response.LanguageResponse;
import com.github.saphyra.apphub.api.user.server.UserDataController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeEmailService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangePasswordService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeUsernameService;
import com.github.saphyra.apphub.service.user.data.service.account.DeleteAccountService;
import com.github.saphyra.apphub.service.user.data.service.account.LanguageService;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class UserDataControllerImpl implements UserDataController {
    private final ChangeEmailService changeEmailService;
    private final LanguageService languageService;
    private final ChangePasswordService changePasswordService;
    private final ChangeUsernameService changeUsernameService;
    private final DeleteAccountService deleteAccountService;
    private final RegistrationService registrationService;

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    //TODO fe test
    public void changeLanguage(AccessTokenHeader accessTokenHeader, OneParamRequest<String> language) {
        log.info("{} wants to change his language", accessTokenHeader.getUserId());
        log.debug("Request payload: {}", language);
        languageService.changeLanguage(accessTokenHeader.getUserId(), language.getValue());
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    //TODO fe test
    public void changeEmail(AccessTokenHeader accessTokenHeader, ChangeEmailRequest request) {
        log.info("{} wants to change his email", accessTokenHeader.getUserId());
        changeEmailService.changeEmail(accessTokenHeader.getUserId(), request);
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    //TODO fe test
    public void changeUsername(AccessTokenHeader accessTokenHeader, ChangeUsernameRequest request) {
        log.info("{} wants to change his username", accessTokenHeader.getUserId());
        changeUsernameService.changeUsername(accessTokenHeader.getUserId(), request);
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    //TODO fe test
    public void changePassword(AccessTokenHeader accessTokenHeader, ChangePasswordRequest request) {
        log.info("{} wants to change his password", accessTokenHeader.getUserId());
        changePasswordService.changePassword(accessTokenHeader.getUserId(), request);
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    //TODO fe test
    public void changePassword(AccessTokenHeader accessTokenHeader, OneParamRequest<String> password) {
        log.info("{} wants to delete his account", accessTokenHeader.getUserId());
        deleteAccountService.deleteAccount(accessTokenHeader.getUserId(), password.getValue());
    }

    @Override
    public String getLanguage(UUID userId) {
        return languageService.getLanguage(userId);
    }

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    //TODO fe test
    public List<LanguageResponse> getLanguages(AccessTokenHeader accessTokenHeader) {
        log.info("Querying available languages for user {}", accessTokenHeader.getUserId());
        return languageService.getLanguages(accessTokenHeader.getUserId());
    }

    @Override
    public void register(RegistrationRequest registrationRequest, String locale) {
        log.info("RegistrationRequest arrived for username {} and email {}", registrationRequest.getUsername(), registrationRequest.getEmail());
        registrationService.register(registrationRequest, locale);
    }
}
