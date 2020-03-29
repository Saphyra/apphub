package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.data.request.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.dao.User;
import com.github.saphyra.apphub.service.user.data.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
//TODO
public class RegistrationService {
    private final RegistrationRequestValidator registrationRequestValidator;
    private final UserDao userDao;
    private final UserFactory userFactory;

    public void register(RegistrationRequest registrationRequest) {
        registrationRequestValidator.validate(registrationRequest);

        User user = userFactory.create(registrationRequest.getEmail(), registrationRequest.getUsername(), registrationRequest.getPassword());
        userDao.save(user);
        log.info("User successfully registered with userId {}", user.getUserId());
    }
}