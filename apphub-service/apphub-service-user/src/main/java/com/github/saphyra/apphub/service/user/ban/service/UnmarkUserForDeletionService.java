package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.BanResponse;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnmarkUserForDeletionService {
    private final UserDao userDao;
    private final BanResponseQueryService banResponseQueryService;

    public BanResponse unmarkUserForDeletion(UUID deletedUserId) {
        User user = userDao.findByIdValidated(deletedUserId);

        user.setMarkedForDeletion(false);
        user.setMarkedForDeletionAt(null);

        userDao.save(user);

        return banResponseQueryService.getBans(deletedUserId);
    }
}
