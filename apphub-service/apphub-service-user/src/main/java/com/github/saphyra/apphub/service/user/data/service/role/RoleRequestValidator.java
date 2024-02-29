package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RoleRequestValidator {
    private final UserDao userDao;
    private final UuidConverter uuidConverter;

    void validateQuery(String query){
        ValidationUtil.minLength(query, 3, "query");
    }

    void validate(RoleRequest roleRequest) {
        ValidationUtil.notNull(roleRequest.getUserId(), "userId");
        ValidationUtil.notBlank(roleRequest.getRole(), "role");
        ValidationUtil.notNull(roleRequest.getPassword(), "password");

        if (userDao.findById(uuidConverter.convertDomain(roleRequest.getUserId())).isEmpty()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, "User not found with id " + roleRequest.getUserId());
        }
    }
}
