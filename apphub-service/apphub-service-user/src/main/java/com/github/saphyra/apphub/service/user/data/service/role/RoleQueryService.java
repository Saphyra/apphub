package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.role.UserRoleResponse;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleQueryService {
    private final RoleDao roleDao;
    private final UserDao userDao;
    private final RoleRequestValidator roleRequestValidator;

    public List<UserRoleResponse> getRoles(String queryString) {
        roleRequestValidator.validateQuery(queryString);

        return userDao.getByUsernameOrEmailContainingIgnoreCase(queryString)
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private UserRoleResponse convert(User user) {
        return UserRoleResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .username(user.getUsername())
            .roles(getRoles(user))
            .build();
    }

    private List<String> getRoles(User user) {
        return roleDao.getByUserId(user.getUserId())
            .stream()
            .map(Role::getRole)
            .collect(Collectors.toList());
    }

    public UserRoleResponse getRoles(UUID userId) {
        return convert(userDao.findByIdValidated(userId));
    }
}
