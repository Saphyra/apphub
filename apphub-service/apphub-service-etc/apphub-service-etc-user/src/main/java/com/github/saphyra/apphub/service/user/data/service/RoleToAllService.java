package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.config.properties.AddRoleToAllProperties;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Semaphore;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoleToAllService {
    private static final Semaphore SEMAPHORE = new Semaphore(1, true);

    private final ExecutorServiceBean executorService;
    private final UserDao userDao;
    private final AddRoleToAllProperties properties;
    private final CheckPasswordService checkPasswordService;

    public void addToAll(UUID userId, String password, String role) {
        if (properties.getRestrictedRoles().contains(role)) {
            throw ExceptionFactory.forbiddenOperation(role + " cannot be added to all users.");
        }

        ValidationUtil.notNull(password, "password");

        checkPasswordService.checkPassword(userId, password);

        executorService.execute(() -> {
            try {
                SEMAPHORE.acquireUninterruptibly(1);

                userDao.addRoleToAll(role);
            } finally {
                SEMAPHORE.release(1);
            }

        });
    }

    public void removeFromAll(UUID userId, String password, String role) {
        if (properties.getRestrictedRoles().contains(role)) {
            throw ExceptionFactory.forbiddenOperation(role + " cannot be removed from all users.");
        }

        ValidationUtil.notNull(password, "password");

        checkPasswordService.checkPassword(userId, password);

        executorService.execute(() -> {
            try {
                SEMAPHORE.acquireUninterruptibly(1);

                userDao.deleteRoleFromAll(role);
            } finally {
                SEMAPHORE.release(1);
            }
        });
    }
}
