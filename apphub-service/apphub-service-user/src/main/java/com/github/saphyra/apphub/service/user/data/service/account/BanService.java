package com.github.saphyra.apphub.service.user.data.service.account;

import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BanService {
    private final UserDao userDao;
    private final DeleteAccountService deleteAccountService;
    private final CheckPasswordService checkPasswordService;

    public void banUser(UUID userId, String password, UUID bannedUserId) {
        checkPasswordService.checkPassword(userId, password);

        User bannedUser = userDao.findByIdValidated(bannedUserId);

        deleteAccountService.deleteAccount(bannedUser);
    }
}
