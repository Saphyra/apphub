package com.github.saphyra.apphub.service.utils.sql_generator.service;

import com.github.saphyra.apphub.api.utils.model.sql_generator.HistoryResponse;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.utils.sql_generator.dao.query.QueryDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class HistoryQueryService {
    private final QueryDao queryDao;
    private final DateTimeUtil dateTimeUtil;

    public List<HistoryResponse> getHistory(UUID userId) {
        return queryDao.getByUserId(userId)
            .stream()
            .map(query -> HistoryResponse.builder()
                .queryId(query.getQueryId())
                .queryType(query.getQueryType())
                .label(query.getLabel())
                .createdAt(dateTimeUtil.format(query.getCreatedAt()))
                .favorite(query.isFavorite())
                .build())
            .collect(Collectors.toList());
    }
}
