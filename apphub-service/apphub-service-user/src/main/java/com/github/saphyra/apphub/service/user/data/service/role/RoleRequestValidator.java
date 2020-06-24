package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorMessage;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
class RoleRequestValidator {
    private final UserDao userDao;
    private final UuidConverter uuidConverter;

    void validate(RoleRequest roleRequest) {
        if (isNull(roleRequest.getUserId())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "userId", "must not be null"), "userId must not be null");
        }

        if (isBlank(roleRequest.getRole())) {
            throw new BadRequestException(new ErrorMessage(ErrorCode.INVALID_PARAM.name(), "role", "must not be null or blank"), "role must not be null or blank");
        }

        if (!userDao.findById(uuidConverter.convertDomain(roleRequest.getUserId())).isPresent()) {
            throw new NotFoundException(new ErrorMessage(ErrorCode.USER_NOT_FOUND.name()), "User not found with id " + roleRequest.getUserId());
        }
    }
}
