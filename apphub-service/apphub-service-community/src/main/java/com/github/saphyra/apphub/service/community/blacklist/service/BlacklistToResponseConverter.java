package com.github.saphyra.apphub.service.community.blacklist.service;

import com.github.saphyra.apphub.api.community.model.response.blacklist.BlacklistResponse;
import com.github.saphyra.apphub.api.user.model.response.AccountResponse;
import com.github.saphyra.apphub.service.community.blacklist.dao.Blacklist;
import com.github.saphyra.apphub.service.community.common.AccountClientProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class BlacklistToResponseConverter {
    private final AccountClientProxy accountClientProxy;

    BlacklistResponse convert(Blacklist blacklist) {
        AccountResponse account = accountClientProxy.getAccount(blacklist.getBlockedUserId());

        return BlacklistResponse.builder()
            .blacklistId(blacklist.getBlacklistId())
            .blockedUserId(blacklist.getBlockedUserId())
            .username(account.getUsername())
            .email(account.getEmail())
            .build();
    }
}
