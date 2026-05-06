package com.github.saphyra.apphub.service.user.authorization;

import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationRequest;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResponse;
import com.github.saphyra.apphub.api.etc.user.model.authorization.AuthorizationResult;
import com.github.saphyra.apphub.api.etc.user.server.UserAuthorizationController;
import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.exception.RestException;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.user.ban.service.BanService;
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
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthorizationController implements UserAuthorizationController {
    private final UserDao userDao;
    private final DateTimeUtil dateTimeUtil;
    private final AccessTokenProvider accessTokenProvider;
    private final CheckPasswordService checkPasswordService;
    private final RoleDao roleDao;
    private final BanService banService;

    @Override
    public AuthorizationResponse authorize(AuthorizationRequest request) {
        log.info("Authorizing user {}", request.getUserIdentifier());

        Optional<User> maybeUser = userDao.findByUsernameOrEmail(request.getUserIdentifier())
            .filter(u -> !u.isMarkedForDeletion());
        if (maybeUser.isEmpty()) {
            log.info("User not found by {}", request.getUserIdentifier());

            return AuthorizationResponse.builder()
                .authorizationResult(AuthorizationResult.USER_NOT_FOUND)
                .build();
        }

        User user = maybeUser.get();

        if (nonNull(user.getLockedUntil()) && user.getLockedUntil().isAfter(dateTimeUtil.getCurrentDateTime())) {
            log.info("User {} locked.", user.getUserId());
            return AuthorizationResponse.builder()
                .authorizationResult(AuthorizationResult.USER_LOCKED)
                .userId(user.getUserId())
                .build();
        }

        try {
            accessTokenProvider.set(AccessToken.builder().userId(user.getUserId()).build());
            checkPasswordService.checkPassword(user.getUserId(), request.getPassword());
        } catch (RestException e) {
            if (e.getErrorMessage().getErrorCode() == ErrorCode.INCORRECT_PASSWORD) {
                log.info("Incorrect password for user {}", user.getUserId());
                return AuthorizationResponse.builder()
                    .authorizationResult(AuthorizationResult.INCORRECT_PASSWORD)
                    .userId(user.getUserId())
                    .build();
            }
            throw e;
        } finally {
            accessTokenProvider.clear();
        }

        return AuthorizationResponse.builder()
            .authorizationResult(AuthorizationResult.AUTHORIZED)
            .userId(user.getUserId())
            .roles(getRoles(user.getUserId()))
            .build();
    }

    @Override
    public List<String> getRoles(UUID userId) {
        log.info("Getting roles for user {}", userId);

        userDao.findById(userId)
            .filter(user -> !user.isMarkedForDeletion())
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found or marked for deletion with id " + userId));

        List<String> bannedRoles = banService.getActivelyBannedRolesOf(userId);
        if (!bannedRoles.isEmpty()) {
            log.info("{} has roles {} banned.", userId, bannedRoles);
        }

        List<String> result = roleDao.getByUserId(userId)
            .stream()
            .map(Role::getRole)
            .filter(role -> !bannedRoles.contains(role))
            .toList();
        log.info("{} has roles {}", userId, result);

        return result;
    }
}
