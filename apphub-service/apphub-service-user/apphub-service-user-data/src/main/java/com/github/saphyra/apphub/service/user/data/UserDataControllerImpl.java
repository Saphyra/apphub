package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.data.model.response.InternalUserResponse;
import com.github.saphyra.apphub.api.user.data.server.UserDataController;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.UuidConverter;
import com.github.saphyra.apphub.lib.error_handler.domain.ErrorMessage;
import com.github.saphyra.apphub.lib.error_handler.exception.NotFoundException;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserDataControllerImpl implements UserDataController {
    private final RegistrationService registrationService;
    private final UserDao userDao;
    private final UuidConverter uuidConverter;

    @Override
    public InternalUserResponse findByEmail(String email) {
        log.info("Querying user by email {}", email);
        User user = userDao.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(new ErrorMessage(ErrorCode.USER_NOT_FOUND.name()), "User not found with email " + email));
        return InternalUserResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .username(user.getUsername())
            .passwordHash(user.getPassword())
            .build();
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
