package com.github.saphyra.apphub.service.platform.main_gateway.service.authorization.authentication;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.saphyra.apphub.api.etc.user.client.DisabledRoleClient;
import com.github.saphyra.apphub.api.etc.user.model.role.DisabledRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class DisabledRoleCache {
    private static final String CACHE_KEY = "disabled_roles";

    private final DisabledRoleClient client;
    private final Cache<String, List<Role>> cache;

    public DisabledRoleCache(DisabledRoleClient disabledRoleClient) {
        this.client = disabledRoleClient;
        cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();
    }

    public List<Role> get() {
        return cache.get(CACHE_KEY, s -> getDisabledRoles());
    }

    private List<Role> getDisabledRoles() {
        List<Role> result = client.getDisabledRoles()
            .stream()
            .filter(DisabledRoleResponse::isDisabled)
            .map(DisabledRoleResponse::getRole)
            .toList();

        log.info("Disabled roles queried: {}", result);

        return result;
    }

    public void clear() {
        cache.invalidate(CACHE_KEY);
    }
}
