package com.github.saphyra.apphub.service.custom.elite_base.service.star_system;

import com.github.saphyra.apphub.api.elite_base.server.StarSystemController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystemSuggestionListCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
class StarSystemControllerImpl implements StarSystemController {
    private final StarSystemSuggestionListCache starSystemSuggestionListCache;

    @Override
    public Map<UUID, String> search(OneParamRequest<String> query, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to query star systems by query {}", accessTokenHeader.getUserId(), query);

        ValidationUtil.minLength(query.getValue(), 3, "query");

        return starSystemSuggestionListCache.get(query.getValue().toLowerCase())
            .orElse(Collections.emptyList())
            .stream()
            .collect(Collectors.toMap(StarSystem::getId, StarSystem::getStarName));
    }
}
