package com.github.saphyra.apphub.lib.security.access_token;

import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccessTokenProvider implements AutoCloseable {
    private static final ThreadLocal<AccessTokenHeader> STORAGE = new ThreadLocal<>();

    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final UuidConverter uuidConverter;

    public String getUserIdAsString() {
        return uuidConverter.convertDomain(get().getUserId());
    }

    public AutoCloseable set(AccessTokenHeader accessTokenHeader) {
        STORAGE.set(accessTokenHeader);

        return this;
    }

    public Optional<AccessTokenHeader> getOptional() {
        return Optional.ofNullable(STORAGE.get());
    }

    public Optional<String> getAsStringOptional() {
        return Optional.ofNullable(STORAGE.get())
            .map(accessTokenHeaderConverter::convertDomain);
    }

    public AccessTokenHeader get() {
        return getOptional().orElseThrow(() -> new IllegalStateException("AccessTokenHeader is not available for the current thread."));
    }

    public String getAsString() {
        return accessTokenHeaderConverter.convertDomain(get());
    }

    public void clear() {
        STORAGE.remove();
    }

    @Override
    public void close() throws Exception {
        clear();
    }
}
