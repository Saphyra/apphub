package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.api.etc.user.model.account.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.dao.user.UserFactory;
import com.github.saphyra.apphub.service.user.data.service.validator.RegistrationRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRequestValidator registrationRequestValidator;
    private final UserFactory userFactory;
    private final UserDao userDao;

    public void register(RegistrationRequest registrationRequest) {
        registrationRequestValidator.validate(registrationRequest);

        User user = userFactory.create(registrationRequest.getEmail(), registrationRequest.getUsername(), registrationRequest.getPassword(), registrationRequest.getLanguage());
        userDao.saveNew(user);
        log.info("User successfully registered with userId {}", user.getUserId());
    }
}