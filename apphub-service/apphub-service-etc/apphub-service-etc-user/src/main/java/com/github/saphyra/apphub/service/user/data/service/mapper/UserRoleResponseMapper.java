package com.github.saphyra.apphub.service.user.data.service.mapper;

import com.github.saphyra.apphub.api.etc.user.model.role.UserRoleResponse;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRoleResponseMapper {
    public List<UserRoleResponse> map(List<User> users) {
        return users.stream()
            .map(this::map)
            .toList();
    }

    public UserRoleResponse map(User user) {
        return UserRoleResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .username(user.getUsername())
            .roles(user.getRoles())
            .build();
    }
}
