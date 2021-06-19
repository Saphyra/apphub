package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
class RoleRequestValidator {
    private final UserDao userDao;
    private final UuidConverter uuidConverter;

    void validate(RoleRequest roleRequest) {
        if (isNull(roleRequest.getUserId())) {
            throw ExceptionFactory.invalidParam("userId", "must not be null");
        }

        if (isBlank(roleRequest.getRole())) {
            throw ExceptionFactory.invalidParam("role", "must not be null or blank");
        }

        if (!userDao.findById(uuidConverter.convertDomain(roleRequest.getUserId())).isPresent()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + roleRequest.getUserId());
        }
    }
}
