package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RevokeBanService {
    private final CheckPasswordService checkPasswordService;
    private final BanDao banDao;

    public void revokeBan(UUID userId, String password, UUID banId) {
        ValidationUtil.notBlank(password, "password");
        checkPasswordService.checkPassword(userId, password);

        banDao.deleteById(banId);
    }
}
