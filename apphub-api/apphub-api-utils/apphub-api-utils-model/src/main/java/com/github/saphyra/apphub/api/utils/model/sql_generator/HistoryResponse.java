package com.github.saphyra.apphub.api.utils.model.sql_generator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HistoryResponse {
    private UUID queryId;
    private QueryType queryType;
    private String label;
    private String createdAt;
    private Boolean favorite;
}
