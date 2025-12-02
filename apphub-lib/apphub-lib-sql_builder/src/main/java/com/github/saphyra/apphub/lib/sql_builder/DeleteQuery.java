package com.github.saphyra.apphub.lib.sql_builder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class DeleteQuery extends AbstractQuery<DeleteQuery>{
    @Override
    public String build() {
        List<String> segments = new ArrayList<>();

        segments.add("DELETE FROM");
        segments.add(from.get());

        if(!conditions.isEmpty()){
            segments.add("WHERE");
        }

        conditions.forEach(segmentProvider -> segments.add(segmentProvider.get()));

        if(nonNull(orderBy)){
            segments.add(orderBy.get());
        }

        if(nonNull(limit)){
            segments.add("LIMIT %s".formatted(limit));

            segments.add("OFFSET %s".formatted(offset));
        }

        return String.join(" ", segments);
    }
}
