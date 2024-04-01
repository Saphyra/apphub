package com.github.saphyra.apphub.service.utils.sql_generator.dao.query;

import com.github.saphyra.apphub.api.utils.model.sql_generator.QueryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Query {
    private final UUID queryId;
    private final UUID userId;
    private final QueryType queryType;
    private final LocalDateTime createdAt;
    private boolean favorite;
    private String label;
}
