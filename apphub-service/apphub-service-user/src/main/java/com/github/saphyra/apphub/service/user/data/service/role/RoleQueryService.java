package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.response.UserRoleResponse;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleQueryService {
    private final RoleDao roleDao;
    private final UserDao userDao;

    public List<UserRoleResponse> getRoles(String queryString) {
        if (isNull(queryString)) {
            throw ExceptionFactory.invalidParam("searchText", "must not be null");
        }

        if (queryString.length() < 3) {
            throw ExceptionFactory.invalidParam("searchText", "too short");
        }

        return userDao.getByUsernameOrEmailContainingIgnoreCase(queryString)
            .stream()
            .map(
                user -> UserRoleResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .roles(getRoles(user))
                    .build()
            ).collect(Collectors.toList());
    }

    private List<String> getRoles(User user) {
        return roleDao.getByUserId(user.getUserId())
            .stream()
            .map(Role::getRole)
            .collect(Collectors.toList());
    }
}
