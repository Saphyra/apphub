package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.request.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.encryption.impl.PasswordService;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class MarkUserForDeletionService {
    private final BanResponseQueryService banResponseQueryService;
    private final UserDao userDao;
    private final PasswordService passwordService;

    public BanResponse markUserForDeletion(UUID deletedUserId, MarkUserForDeletionRequest request, UUID userId) {
        checkPassword(userId, request.getPassword());
        LocalDateTime markedForDeletionAt = convertTime(request);

        User user = userDao.findByIdValidated(deletedUserId);

        user.setMarkedForDeletion(true);
        user.setMarkedForDeletionAt(markedForDeletionAt);

        userDao.save(user);

        return banResponseQueryService.getBans(deletedUserId);
    }

    private void checkPassword(UUID userId, String password) {
        ValidationUtil.notNull(password, "password");

        User user = userDao.findByIdValidated(userId);

        if (!passwordService.authenticate(password, user.getPassword())) {
            throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.INCORRECT_PASSWORD, "Incorrect password");
        }
    }

    private LocalDateTime convertTime(MarkUserForDeletionRequest request) {
        ValidationUtil.notNull(request.getDate(), "date");
        ValidationUtil.notNull(request.getTime(), "time");

        LocalTime time;

        try {
            String[] split = request.getTime()
                .split(":");

            time = LocalTime.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0);
        } catch (RuntimeException e) {
            throw ExceptionFactory.invalidParam("time", "invalid value");
        }

        return LocalDateTime.of(request.getDate(), time);
    }
}
