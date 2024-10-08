package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BanDetailsResponse;
import com.github.saphyra.apphub.api.user.model.ban.BanResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    private final DateTimeUtil dateTimeUtil;

    public BanResponse getBans(UUID bannedUserId) {
        User bannedUser = userDao.findByIdValidated(bannedUserId);

        List<BanDetailsResponse> bans = banDao.getByUserId(bannedUserId)
            .stream()
            .map(this::map)
            .collect(Collectors.toList());

        return BanResponse.builder()
            .userId(bannedUserId)
            .username(bannedUser.getUsername())
            .email(bannedUser.getEmail())
            .bans(bans)
            .markedForDeletion(bannedUser.isMarkedForDeletion())
            .markedForDeletionAt(Optional.ofNullable(bannedUser.getMarkedForDeletionAt()).map(dateTime -> dateTimeUtil.format(dateTime, false)).orElse(null))
            .build();
    }

    private BanDetailsResponse map(Ban ban) {
        User bannedByUser = userDao.findByIdValidated(ban.getBannedBy());

        return BanDetailsResponse.builder()
            .id(ban.getId())
            .bannedRole(ban.getBannedRole())
            .expiration(Optional.ofNullable(ban.getExpiration()).map(dateTimeUtil::format).orElse(null))
            .permanent(ban.getPermanent())
            .reason(ban.getReason())
            .bannedById(ban.getBannedBy())
            .bannedByUsername(bannedByUser.getUsername())
            .bannedByEmail(bannedByUser.getEmail())
            .build();
    }
}
