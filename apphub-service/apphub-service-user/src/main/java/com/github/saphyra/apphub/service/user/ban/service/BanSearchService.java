package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BanSearchResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BanSearchService {
    private final UserDao userDao;
    private final BanDao banDao;

    public List<BanSearchResponse> search(String query) {
        ValidationUtil.minLength(query, 3, "query");

        return userDao.getByUsernameOrEmailContainingIgnoreCase(query)
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private BanSearchResponse convert(User user) {
        return BanSearchResponse.builder()
            .userId(user.getUserId())
            .username(user.getUsername())
            .email(user.getEmail())
            .markedForDeletion(user.isMarkedForDeletion())
            .bannedRoles(getBannedRoles(user.getUserId()))
            .build();
    }

    private List<String> getBannedRoles(UUID userId) {
        return banDao.getByUserId(userId)
            .stream()
            .map(Ban::getBannedRole)
            .collect(Collectors.toList());
    }
}
