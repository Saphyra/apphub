package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BanResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RevokeBanService {
    private final CheckPasswordService checkPasswordService;
    private final BanDao banDao;
    private final DateTimeUtil dateTimeUtil;
    private final BanResponseQueryService banResponseQueryService;

    public BanResponse revokeBan(UUID userId, String password, UUID banId) {
        ValidationUtil.notBlank(password, "password");
        checkPasswordService.checkPassword(userId, password);

        Ban ban = banDao.findByIdValidated(banId);

        banDao.delete(ban);

        return banResponseQueryService.getBans(ban.getUserId());
    }

    @Transactional
    public void revokeExpiredBans() {
        LocalDateTime expiration = dateTimeUtil.getCurrentDateTime();

        banDao.deleteExpired(expiration);
    }
}
