package com.github.saphyra.apphub.api.utils.model.sql_generator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QueryModel {
    private QueryType queryType;
    private String label;
    private List<SegmentModel> segments;
}
