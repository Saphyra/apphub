package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.model.request.ChangeEmailRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangePasswordRequest;
import com.github.saphyra.apphub.api.user.model.request.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.user.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.api.user.server.AccountController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeEmailService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangePasswordService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeUsernameService;
import com.github.saphyra.apphub.service.user.data.service.account.DeleteAccountService;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
class AccountControllerImpl implements AccountController {
    private final ChangeEmailService changeEmailService;
    private final ChangePasswordService changePasswordService;
    private final ChangeUsernameService changeUsernameService;
    private final DeleteAccountService deleteAccountService;
    private final RegistrationService registrationService;
    private final UserDao userDao;

    @Override
    public void changeEmail(AccessTokenHeader accessTokenHeader, ChangeEmailRequest request) {
        log.info("{} wants to change his email", accessTokenHeader.getUserId());
        changeEmailService.changeEmail(accessTokenHeader.getUserId(), request);
    }

    @Override
    public void changeUsername(AccessTokenHeader accessTokenHeader, ChangeUsernameRequest request) {
        log.info("{} wants to change his username", accessTokenHeader.getUserId());
        changeUsernameService.changeUsername(accessTokenHeader.getUserId(), request);
    }

    @Override
    public void changePassword(AccessTokenHeader accessTokenHeader, ChangePasswordRequest request) {
        log.info("{} wants to change his password", accessTokenHeader.getUserId());
        changePasswordService.changePassword(accessTokenHeader.getUserId(), request);
    }

    @Override
    public void deleteAccount(AccessTokenHeader accessTokenHeader, OneParamRequest<String> password) {
        log.info("{} wants to delete his account", accessTokenHeader.getUserId());
        deleteAccountService.deleteAccount(accessTokenHeader.getUserId(), password.getValue());
    }

    @Override
    public void register(RegistrationRequest registrationRequest, String locale) {
        log.info("RegistrationRequest arrived for username {} and email {}", registrationRequest.getUsername(), registrationRequest.getEmail());
        registrationService.register(registrationRequest, locale);
    }

    @Override
    public String getUsernameByUserId(UUID userId) {
        log.info("Querying name of user {}", userId);
        return userDao.findByIdValidated(userId)
            .getUsername();
    }

    @Override
    public List<AccountResponse> searchAccount(OneParamRequest<String> search, AccessTokenHeader accessTokenHeader) {
        String searchText = search.getValue();
        log.info("{} wants to query users by {}", accessTokenHeader.getUserId(), searchText);
        ValidationUtil.minLength(searchText, 3, "value");

        return userDao.getByUsernameOrEmailContainingIgnoreCase(searchText)
            .stream()
            .filter(user -> !user.getUserId().equals(accessTokenHeader.getUserId()))
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private AccountResponse convert(User user) {
        return AccountResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .username(user.getUsername())
            .build();
    }

    @Override
    //TODO unit test
    public AccountResponse getAccount(UUID userId) {
        return userDao.findById(userId)
            .map(this::convert)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + userId));
    }

    @Override
    //TODO unit test
    public boolean userExists(UUID userId) {
        return userDao.findById(userId)
            .isPresent();
    }
}
