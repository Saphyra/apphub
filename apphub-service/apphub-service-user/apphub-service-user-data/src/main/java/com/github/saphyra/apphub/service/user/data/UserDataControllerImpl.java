package com.github.saphyra.apphub.service.user.data;

import com.github.saphyra.apphub.api.user.data.model.request.RegistrationRequest;
import com.github.saphyra.apphub.api.user.data.model.response.InternalUserResponse;
import com.github.saphyra.apphub.api.user.data.server.UserDataController;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import com.github.saphyra.apphub.service.user.data.service.register.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
//TODO unit test
//TODO int test
//TODO api test
//TODO fe test
public class UserDataControllerImpl implements UserDataController {
    private final UserDao userDao;
    private final RegistrationService registrationService;

    @Override
    public InternalUserResponse findByEmail(String email) {
        log.info("Querying user by email {}", email);
        User user = userDao.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email " + email));
        return InternalUserResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .username(user.getUsername())
            .passwordHash(user.getPassword())
            .build();
    }

    @Override
    public void register(RegistrationRequest registrationRequest) {
        log.info("RegistrationRequest arrived for username {} and email {}", registrationRequest.getUsername(), registrationRequest.getEmail());
        registrationService.register(registrationRequest);
    }
}
