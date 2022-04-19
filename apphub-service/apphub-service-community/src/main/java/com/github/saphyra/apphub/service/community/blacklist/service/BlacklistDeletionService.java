package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.blacklist.dao.BlacklistDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class BlacklistDeletionService {
    private final BlacklistDao blacklistDao;

    public void delete(UUID userId, UUID blacklistId) {
        Blacklist blacklist = blacklistDao.findById(blacklistId)
            .orElseThrow(() -> ExceptionFactory.notLoggedException(HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND, "Blacklist not found with id " + blacklistId));

        if (!blacklist.getUserId().equals(userId)) {
            throw ExceptionFactory.loggedException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN_OPERATION, userId + " must not delete Blacklist " + blacklistId);
        }

        blacklistDao.delete(blacklist);
    }
}
