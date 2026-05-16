package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.etc.user.model.account.AccountResponse;
import com.github.saphyra.apphub.api.etc.user.model.account.ChangeEmailRequest;
import com.github.saphyra.apphub.api.etc.user.model.account.ChangePasswordRequest;
import com.github.saphyra.apphub.api.etc.user.model.account.ChangeUsernameRequest;
import com.github.saphyra.apphub.api.etc.user.model.account.RegistrationRequest;
import com.github.saphyra.apphub.api.etc.user.server.AccountController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_domain.OneParamResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.RegistrationService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeEmailService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangePasswordService;
import com.github.saphyra.apphub.service.user.data.service.account.ChangeUsernameService;
import com.github.saphyra.apphub.service.user.data.service.account.DeleteAccountService;
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
public class AccountControllerImpl implements AccountController {
    private final ChangeEmailService changeEmailService;
    private final ChangePasswordService changePasswordService;
    private final ChangeUsernameService changeUsernameService;
    private final DeleteAccountService deleteAccountService;
    private final RegistrationService registrationService;
    private final UserDao userDao;

    @Override
    public AccountResponse changeEmail(AccessToken accessToken, ChangeEmailRequest request) {
        log.info("{} wants to change his email", accessToken.getUserId());
        changeEmailService.changeEmail(accessToken.getUserId(), request);

        return getAccount(accessToken);
    }

    @Override
    public AccountResponse changeUsername(AccessToken accessToken, ChangeUsernameRequest request) {
        log.info("{} wants to change his username", accessToken.getUserId());
        changeUsernameService.changeUsername(accessToken.getUserId(), request);

        return getAccount(accessToken);
    }

    @Override
    public void changePassword(AccessToken accessToken, ChangePasswordRequest request) {
        log.info("{} wants to change his password", accessToken.getUserId());
        changePasswordService.changePassword(accessToken.getUserId(), request);
    }

    @Override
    public void deleteAccount(AccessToken accessToken, OneParamRequest<String> password) {
        log.info("{} wants to delete his account", accessToken.getUserId());
        deleteAccountService.deleteAccount(accessToken.getUserId(), password.getValue());
    }

    @Override
    public void register(RegistrationRequest registrationRequest) {
        log.info("{} arrived", registrationRequest);
        registrationService.register(registrationRequest);
    }

    @Override
    public OneParamResponse<String> getUsernameByUserId(AccessToken accessToken) {
        log.info("Querying name of user {}", accessToken.getUserId());
        String username = userDao.findByUserIdValidated(accessToken.getUserId())
            .getUsername();
        return new OneParamResponse<>(username);
    }

    @Override
    public List<AccountResponse> searchAccount(OneParamRequest<String> search, Boolean includeMarkedForDeletion, Boolean includeSelf, AccessToken accessToken) {
        String searchText = search.getValue();
        log.info("{} wants to query users by {}", accessToken.getUserId(), searchText);
        ValidationUtil.minLength(searchText, 3, "value");

        return userDao.findByUserIdentifier(searchText)
            .stream()
            .filter(user -> includeSelf || !user.getUserId().equals(accessToken.getUserId()))
            .filter(user -> includeMarkedForDeletion || !user.isMarkedForDeletion())
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private AccountResponse convert(User user) {
        return AccountResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .username(user.getUsername())
            .locale(user.getLanguage())
            .build();
    }

    @Override
    public AccountResponse getAccountInternal(UUID userId) {
        return userDao.findByUserId(userId)
            .map(this::convert)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + userId));
    }

    @Override
    public AccountResponse getAccount(AccessToken accessToken) {
        return convert(userDao.findByUserIdValidated(accessToken.getUserId()));
    }

    @Override
    public boolean userExists(UUID userId) {
        return userDao.findByUserId(userId)
            .filter(user -> !user.isMarkedForDeletion())
            .isPresent();
    }
}
