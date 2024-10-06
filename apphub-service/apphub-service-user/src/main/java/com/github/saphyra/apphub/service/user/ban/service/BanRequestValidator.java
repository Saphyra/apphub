package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BanRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
class BanRequestValidator {
    public void validate(BanRequest request) {
        ValidationUtil.notNull(request.getBannedUserId(), "bannedUserId");
        ValidationUtil.notBlank(request.getBannedRole(), "bannedRole");
        ValidationUtil.notNull(request.getPermanent(), "permanent");
        ValidationUtil.notBlank(request.getReason(), "reason");
        ValidationUtil.notBlank(request.getPassword(), "password");

        if (!request.getPermanent()) {
            ValidationUtil.notNull(request.getDuration(), "duration");

            if (request.getDuration() < 1) {
                throw ExceptionFactory.invalidParam("duration", "too low");
            }

            ValidationUtil.convertToEnumChecked(request.getChronoUnit(), ChronoUnit::valueOf, "chronoUnit");
        }
    }
}
