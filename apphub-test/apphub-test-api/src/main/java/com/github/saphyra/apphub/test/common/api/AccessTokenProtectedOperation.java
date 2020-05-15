package com.github.saphyra.apphub.test.common.api;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class AccessTokenProtectedOperation {
    private final AccessTokenProvider accessTokenProvider;

    public void operate(AccessTokenHeader header, Runnable task) {
        try {
            accessTokenProvider.set(header);
            task.run();
        } finally {
            accessTokenProvider.clear();
        }
    }

    public <T> T getResult(AccessTokenHeader header, Supplier<T> task) {
        try {
            accessTokenProvider.set(header);
            return task.get();
        } finally {
            accessTokenProvider.clear();
        }
    }
}
