package com.github.saphrya.apphub.service.platform.authorization.service;

import com.github.saphyra.apphub.api.platform.authorization.model.LoginRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
@RequiredArgsConstructor
@Slf4j
class LoginRequestValidator {
    void validate(LoginRequest loginRequest) {
        if (isBlank(loginRequest.getUserIdentifier()) || isEmpty(loginRequest.getPassword())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.BAD_CREDENTIALS);
        }

        ValidationUtil.notNull(loginRequest.getRememberMe(), "rememberMe");
    }
}
