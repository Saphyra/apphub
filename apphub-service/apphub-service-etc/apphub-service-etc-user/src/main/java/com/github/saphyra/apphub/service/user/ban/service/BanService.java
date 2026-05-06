package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.etc.user.model.ban.BanRequest;
import com.github.saphyra.apphub.api.etc.user.model.ban.BanResponse;
import com.github.saphyra.apphub.api.platform.authorization.client.AuthorizationClient;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BanService {
    private final BanRequestValidator banRequestValidator;
    private final CheckPasswordService checkPasswordService;
    private final BanFactory banFactory;
    private final BanDao banDao;
    private final BanResponseQueryService banResponseQueryService;
    private final AuthorizationClient authorizationClient;
    private final DateTimeUtil dateTimeUtil;

    public BanResponse ban(UUID userId, BanRequest request) {
        banRequestValidator.validate(request);
        checkPasswordService.checkPassword(userId, request.getPassword());

        Ban ban = banFactory.create(userId, request);
        banDao.save(ban);

        authorizationClient.invalidateAllAccessTokens(request.getBannedUserId());

        return banResponseQueryService.getBans(request.getBannedUserId());
    }

    public List<String> getActivelyBannedRolesOf(UUID userId) {
        LocalDateTime currentTime =  dateTimeUtil.getCurrentDateTime();

        return banDao.getByUserId(userId)
            .stream()
            .filter(ban -> ban.isPermanent() || ban.getExpiration().isAfter(currentTime))
            .map(Ban::getBannedRole)
            .toList();
    }
}
