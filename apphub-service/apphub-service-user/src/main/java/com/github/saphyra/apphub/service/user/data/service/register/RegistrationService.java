package com.github.saphyra.apphub.service.user.data.service.register;

import com.github.saphyra.apphub.api.user.model.login.RegistrationRequest;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.role.RoleAdditionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService {
    private final RegistrationRequestValidator registrationRequestValidator;
    private final RoleAdditionService roleAdditionService;
    private final RegistrationProperties registrationProperties;
    private final UserDao userDao;
    private final UserFactory userFactory;

    public void register(RegistrationRequest registrationRequest, String locale) {
        registrationRequestValidator.validate(registrationRequest);

        User user = userFactory.create(registrationRequest.getEmail(), registrationRequest.getUsername(), registrationRequest.getPassword(), locale);
        userDao.save(user);
        log.info("User successfully registered with userId {}", user.getUserId());
        addDefaultRoles(user.getUserId());
    }

    private void addDefaultRoles(UUID userId) {
        registrationProperties.getDefaultRoles().forEach(role -> roleAdditionService.addRole(userId, role));
    }
}