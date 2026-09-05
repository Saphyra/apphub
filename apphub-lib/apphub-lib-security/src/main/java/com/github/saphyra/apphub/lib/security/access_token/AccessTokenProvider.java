package com.github.saphyra.apphub.lib.security.access_token;

import com.github.saphyra.apphub.lib.common_domain.AccessToken;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccessTokenProvider implements AutoCloseable {
    private static final ThreadLocal<AccessToken> STORAGE = new ThreadLocal<>();

    private final AccessTokenHeaderConverter accessTokenHeaderConverter;
    private final UuidConverter uuidConverter;

    public String getUserIdAsString() {
        return uuidConverter.convertDomain(get().getUserId());
    }

    public AutoCloseable set(AccessToken accessToken) {
        STORAGE.set(accessToken);

        return this;
    }

    public Optional<AccessToken> getOptional() {
        return Optional.ofNullable(STORAGE.get());
    }

    public Optional<String> getAsStringOptional() {
        return Optional.ofNullable(STORAGE.get())
            .map(accessTokenHeaderConverter::convertDomain);
    }

    public AccessToken get() {
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

    public AutoCloseable set(UUID userId) {
        AccessToken accessToken = AccessToken.builder()
            .userId(userId)
            .build();

        return set(accessToken);
    }
}
