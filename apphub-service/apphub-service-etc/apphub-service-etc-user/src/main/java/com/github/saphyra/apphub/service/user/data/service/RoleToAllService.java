package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.config.properties.AddRoleToAllProperties;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class RoleToAllService {
    private final ExecutorServiceBean executorService;
    private final UserDao userDao;
    private final AddRoleToAllProperties properties;
    private final CheckPasswordService checkPasswordService;

    @Builder
    RoleToAllService(
        UserDao userDao,
        ExecutorServiceBeanFactory executorServiceBeanFactory,
        AddRoleToAllProperties properties,
        CheckPasswordService checkPasswordService
    ) {
        this.userDao = userDao;
        this.executorService = executorServiceBeanFactory.create(Executors.newSingleThreadExecutor());
        this.properties = properties;
        this.checkPasswordService = checkPasswordService;
    }

    public void addToAll(UUID userId, String password, String role) {
        if (properties.getRestrictedRoles().contains(role)) {
            throw ExceptionFactory.forbiddenOperation(role + " cannot be added to all users.");
        }

        ValidationUtil.notNull(password, "password");

        checkPasswordService.checkPassword(userId, password);

        executorService.execute(() -> userDao.addRoleToAll(role));
    }

    public void removeFromAll(UUID userId, String password, String role) {
        if (properties.getRestrictedRoles().contains(role)) {
            throw ExceptionFactory.forbiddenOperation(role + " cannot be removed from all users.");
        }

        ValidationUtil.notNull(password, "password");

        checkPasswordService.checkPassword(userId, password);

        executorService.execute(() -> userDao.deleteRoleFromAll(role));
    }
}
