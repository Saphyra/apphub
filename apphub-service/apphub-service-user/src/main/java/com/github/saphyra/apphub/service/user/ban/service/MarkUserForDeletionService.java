package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.request.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class MarkUserForDeletionService {
    private final BanResponseQueryService banResponseQueryService;
    private final UserDao userDao;
    private final CheckPasswordService checkPasswordService;

    public BanResponse markUserForDeletion(UUID deletedUserId, MarkUserForDeletionRequest request, UUID userId) {
        ValidationUtil.notNull(request.getPassword(), "password");

        checkPasswordService.checkPassword(userId, request.getPassword());
        LocalDateTime markedForDeletionAt = convertTime(request);

        User user = userDao.findByIdValidated(deletedUserId);
        user.setMarkedForDeletion(true);
        user.setMarkedForDeletionAt(markedForDeletionAt);

        userDao.save(user);

        return banResponseQueryService.getBans(deletedUserId);
    }

    private LocalDateTime convertTime(MarkUserForDeletionRequest request) {
        return  ValidationUtil.parse(request.getMarkForDeletionAt(), o -> LocalDateTime.parse(o.toString() + ":00"), "markForDeletionAt");
    }
}
