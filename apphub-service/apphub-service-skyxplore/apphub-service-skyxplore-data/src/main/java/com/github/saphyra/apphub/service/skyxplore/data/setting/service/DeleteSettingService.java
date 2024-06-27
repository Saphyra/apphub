package com.github.saphyra.apphub.service.skyxplore.data.setting.service;

import com.github.saphyra.apphub.api.skyxplore.model.data.setting.SettingIdentifier;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.data.common.GameProxy;
import com.github.saphyra.apphub.service.skyxplore.data.setting.dao.SettingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteSettingService {
    private final GameProxy gameProxy;
    private final SettingDao settingDao;

    public void delete(UUID userId, SettingIdentifier request) {
        UUID gameId = gameProxy.getGameIdValidated(userId);

        settingDao.findByUserIdAndGameIdAndTypeAndLocation(userId, gameId, request.getType(), request.getLocation())
            .ifPresentOrElse(
                settingDao::delete,
                () -> {
                    throw ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Setting not found for identifier " + request);
                }
            );
    }
}
