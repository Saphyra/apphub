package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RoleToAllService {
    private final ExecutorServiceBean executorService;
    private final RoleDao roleDao;
    private final UserDao userDao;
    private final RoleFactory roleFactory;
    private final AddRoleToAllProperties properties;
    private final CheckPasswordService checkPasswordService;

    @Builder
    public RoleToAllService(
        RoleDao roleDao,
        UserDao userDao,
        RoleFactory roleFactory,
        ExecutorServiceBeanFactory executorServiceBeanFactory,
        AddRoleToAllProperties properties,
        CheckPasswordService checkPasswordService
    ) {
        this.roleDao = roleDao;
        this.userDao = userDao;
        this.roleFactory = roleFactory;
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

        executorService.execute(() -> addLogic(role));
    }

    @Transactional
    public void removeFromAll(UUID userId, String password, String role) {
        if (properties.getRestrictedRoles().contains(role)) {
            throw ExceptionFactory.forbiddenOperation(role + " cannot be removed from all users.");
        }

        ValidationUtil.notNull(password, "password");

        checkPasswordService.checkPassword(userId, password);

        executorService.execute(() -> roleDao.deleteByRole(role));
    }

    private void addLogic(String role) {
        List<Role> roles = userDao.findAll()
            .stream()
            .filter(user -> roleDao.findByUserIdAndRole(user.getUserId(), role).isEmpty())
            .map(user -> roleFactory.create(user.getUserId(), role))
            .collect(Collectors.toList());

        roleDao.saveAll(roles);
    }
}
