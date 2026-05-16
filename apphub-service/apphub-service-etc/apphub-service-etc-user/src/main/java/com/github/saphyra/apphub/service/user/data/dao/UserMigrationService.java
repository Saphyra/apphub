package com.github.saphyra.apphub.service.user.data.dao;

import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.user.data.dao.role_deprecated.RoleDto;
import com.github.saphyra.apphub.service.user.data.dao.role_deprecated.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.dao.user_deprecated.DeprecatedUser;
import com.github.saphyra.apphub.service.user.data.dao.user_deprecated.DeprecatedUserDao;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class UserMigrationService {
    private final DeprecatedUserDao deprecatedUserDao;
    private final RoleDao roleDao;
    private final UserDao userDao;
    private final ErrorReporterService errorReporterService;

    @PostConstruct
    void migrate() {
        log.info("Starting user migration");

        deprecatedUserDao.findAll()
            .forEach(this::migrate);

        log.info("User migration finished.");
    }

    private void migrate(DeprecatedUser deprecatedUser) {
        log.info("Migrating user {}", deprecatedUser.getUserId());
        try {
            List<RoleDto> roleDtos = roleDao.getByUserId(deprecatedUser.getUserId());

            User newUser = User.builder()
                .userId(deprecatedUser.getUserId())
                .email(deprecatedUser.getEmail())
                .username(deprecatedUser.getUsername())
                .password(deprecatedUser.getPassword())
                .language(deprecatedUser.getLanguage())
                .markedForDeletionAt(deprecatedUser.getMarkedForDeletionAt())
                .passwordFailureCount(deprecatedUser.getPasswordFailureCount())
                .lockedUntil(deprecatedUser.getLockedUntil())
                .roles(roleDtos.stream().map(RoleDto::getRole).map(Role::valueOf).toList())
                .build();

            userDao.saveNew(newUser);
            roleDtos.forEach(roleDao::delete);
            deprecatedUserDao.delete(deprecatedUser);

            log.info("User {} successfully migrated.", deprecatedUser.getUserId());
        } catch (Exception e) {
            errorReporterService.report("Failed migrating user " + deprecatedUser.getUserId(), e);
        }
    }
}
