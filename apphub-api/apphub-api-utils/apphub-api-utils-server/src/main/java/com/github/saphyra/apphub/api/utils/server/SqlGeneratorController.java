package com.github.saphyra.apphub.api.utils.server;

import com.github.saphyra.apphub.api.utils.model.sql_generator.QueryModel;
import com.github.saphyra.apphub.api.utils.model.sql_generator.HistoryResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

public interface SqlGeneratorController {
    @GetMapping(Endpoints.UTILS_SQL_GENERATOR_GET_HISTORY)
    List<HistoryResponse> getHistory(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PostMapping(Endpoints.UTILS_SQL_GENERATOR_MARK_QUERY_FAVORITE)
    List<HistoryResponse> markQueryFavorite(@RequestBody OneParamRequest<Boolean> favorite, @PathVariable("queryId") UUID queryId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @PutMapping(Endpoints.UTILS_SQL_GENERATOR_CREATE_QUERY)
    List<HistoryResponse> createQuery(@RequestBody QueryModel request, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @DeleteMapping(Endpoints.UTILS_SQL_GENERATOR_DELETE_QUERY)
    List<HistoryResponse> deleteQuery(@PathVariable("queryId") UUID queryId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);

    @GetMapping(Endpoints.UTILS_SQL_GENERATOR_GET_QUERY)
    QueryModel getQuery(@PathVariable("queryId") UUID queryId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
