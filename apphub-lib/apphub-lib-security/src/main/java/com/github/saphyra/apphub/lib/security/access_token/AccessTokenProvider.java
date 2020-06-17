package com.github.saphyra.apphub.lib.security.access_token;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccessTokenProvider {
    private static final ThreadLocal<AccessTokenHeader> STORAGE = new ThreadLocal<>();

    public void set(AccessTokenHeader accessTokenHeader) {
        STORAGE.set(accessTokenHeader);
    }

    public Optional<AccessTokenHeader> getOptional() {
        return Optional.ofNullable(STORAGE.get());
    }

    public AccessTokenHeader get() {
        return getOptional().orElseThrow(() -> new IllegalStateException("AccessTokenHeader is not available for the current thread."));
    }

    public void clear() {
        STORAGE.remove();
    }
}
