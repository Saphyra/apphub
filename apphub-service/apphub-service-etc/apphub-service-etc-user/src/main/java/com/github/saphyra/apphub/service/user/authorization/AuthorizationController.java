package com.github.saphyra.apphub.service.user.authorization;

import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import com.github.saphyra.apphub.api.etc.user.server.UserAuthorizationController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.exception.RestException;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class AuthorizationController implements UserAuthorizationController {
    private final UserDao userDao;
    private final DateTimeUtil dateTimeUtil;
    private final AccessTokenProvider accessTokenProvider;
    private final CheckPasswordService checkPasswordService;
    private final RoleDao roleDao;

    @Override
    public AuthorizationResponse authorize(AuthorizationRequest request) {
        log.info("Authorizing user {}", request.getUserIdentifier());

        User user = userDao.findByUsernameOrEmail(request.getUserIdentifier())
            .filter(u -> !u.isMarkedForDeletion())
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, String.format("User not found by %s", request.getUserIdentifier())));

        if (nonNull(user.getLockedUntil()) && user.getLockedUntil().isAfter(dateTimeUtil.getCurrentDateTime())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.ACCOUNT_LOCKED, "User account locked.");
        }

        try {
            accessTokenProvider.set(AccessToken.builder().userId(user.getUserId()).build());
            checkPasswordService.checkPassword(user.getUserId(), request.getPassword());
        } catch (RestException e) {
            if (e.getErrorMessage().getErrorCode() == ErrorCode.INCORRECT_PASSWORD) {
                throw ExceptionFactory.notLoggedException(HttpStatus.UNAUTHORIZED, ErrorCode.BAD_CREDENTIALS, "Incorrect password");
            }
            throw e;
        } finally {
            accessTokenProvider.clear();
        }

        return AuthorizationResponse.builder()
            .userId(user.getUserId())
            .roles(getRoles(user.getUserId()))
            .build();
    }

    @Override
    public List<String> getRoles(UUID userId) {
        log.info("Getting roles for user {}", userId);

        return roleDao.getByUserId(userId)
            .stream()
            .map(Role::getRole)
            .toList();
    }
}
