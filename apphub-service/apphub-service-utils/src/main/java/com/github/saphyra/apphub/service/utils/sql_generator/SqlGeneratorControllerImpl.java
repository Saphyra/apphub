package com.github.saphyra.apphub.service.utils.sql_generator;

import com.github.saphyra.apphub.api.utils.model.sql_generator.QueryModel;
import com.github.saphyra.apphub.api.utils.model.sql_generator.HistoryResponse;
import com.github.saphyra.apphub.api.utils.server.SqlGeneratorController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.utils.sql_generator.service.HistoryQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SqlGeneratorControllerImpl implements SqlGeneratorController {
    private final HistoryQueryService historyQueryService;

    @Override
    public List<HistoryResponse> getHistory(AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his SQL generator history.", accessTokenHeader.getUserId());
        return historyQueryService.getHistory(accessTokenHeader.getUserId());
    }

    @Override
    public List<HistoryResponse> markQueryFavorite(OneParamRequest<Boolean> favorite, UUID queryId, AccessTokenHeader accessTokenHeader) {
        //TODO implement
        return getHistory(accessTokenHeader);
    }

    @Override
    public List<HistoryResponse> createQuery(QueryModel request, AccessTokenHeader accessTokenHeader) {
        //TODO implement
        return getHistory(accessTokenHeader);
    }

    @Override
    public List<HistoryResponse> deleteQuery(UUID queryId, AccessTokenHeader accessTokenHeader) {
        //TODO implement
        return getHistory(accessTokenHeader);
    }

    @Override
    public QueryModel getQuery(UUID queryId, AccessTokenHeader accessTokenHeader) {
        return null;//TODO implement
    }
}
