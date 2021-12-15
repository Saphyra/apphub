package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.request.BanRequest;
import com.github.saphyra.apphub.service.user.ban.dao.Ban;
import com.github.saphyra.apphub.service.user.ban.dao.BanDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BanService {
    private final BanRequestValidator banRequestValidator;
    private final CheckPasswordService checkPasswordService;
    private final BanFactory banFactory;
    private final BanDao banDao;

    public void ban(UUID userId, BanRequest request) {
        banRequestValidator.validate(request);
        checkPasswordService.checkPassword(userId, request.getPassword());

        Ban ban = banFactory.create(userId, request);
        banDao.save(ban);
    }
}
