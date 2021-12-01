package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BanResponseQueryService {
    private final BanDao banDao;
    private final UserDao userDao;

    public List<BanResponse> getBans(UUID bannedUserId) {
        return banDao.getByUserId(bannedUserId)
            .stream()
            .map(this::map)
            .collect(Collectors.toList());
    }

    private BanResponse map(Ban ban) {
        User bannedUser = userDao.findByIdValidated(ban.getUserId());
        User bannedByUser = userDao.findByIdValidated(ban.getBannedBy());

        return BanResponse.builder()
            .id(ban.getId())
            .userId(ban.getUserId())
            .username(bannedUser.getUsername())
            .email(bannedUser.getEmail())
            .bannedRole(ban.getBannedRole())
            .expiration(Optional.ofNullable(ban.getExpiration()).map(localDateTime -> localDateTime.toEpochSecond(ZoneOffset.UTC)).orElse(null))
            .permanent(ban.getPermanent())
            .reason(ban.getReason())
            .bannedById(ban.getBannedBy())
            .bannedByUsername(bannedByUser.getUsername())
            .bannedByEmail(bannedByUser.getEmail())
            .build();
    }
}
