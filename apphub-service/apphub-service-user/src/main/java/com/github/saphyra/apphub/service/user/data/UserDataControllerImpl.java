package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.server.UserDataController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.NotFoundException;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeEmailService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeLanguageService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangePasswordService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeUsernameService;
import com.github.saphyra.apphub.service.user.data.service.account.DeleteAccountService;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
class UserDataControllerImpl implements UserDataController {
    private final ChangeEmailService changeEmailService;
    private final ChangeLanguageService changeLanguageService;
    private final ChangePasswordService changePasswordService;
    private final ChangeUsernameService changeUsernameService;
    private final DeleteAccountService deleteAccountService;
    private final RegistrationService registrationService;
    private final UserDao userDao;
    private final UuidConverter uuidConverter;

    @Override
    //TODO unit test
    //TODO int test
    //TODO api test
    //TODO fe test
    public void changeLanguage(AccessTokenHeader accessTokenHeader, OneParamRequest<String> language) {
        log.info("{} wants to change his language", accessTokenHeader.getUserId());
        log.debug("Request payload: {}", language);
        changeLanguageService.changeLanguage(accessTokenHeader.getUserId(), language.getValue());
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
        return userDao.findById(uuidConverter.convertDomain(userId))
            .map(User::getLanguage)
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.USER_NOT_FOUND.name()), String.format("User not found with id %s", userId)));
    }

    @Override
    public void register(RegistrationRequest registrationRequest, String locale) {
        log.info("RegistrationRequest arrived for username {} and email {}", registrationRequest.getUsername(), registrationRequest.getEmail());
        registrationService.register(registrationRequest, locale);
    }
}
