package com.github.saphyra.apphub.service.notebook.dao.migration;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class ListItemViewQueryService {
    private final JdbcTemplate jdbcTemplate;
    private final UuidConverter uuidConverter;

    public List<BiWrapper<UUID, UUID>> getAll() {
        String sql = "SELECT list_item_id, user_id FROM notebook.list_item";

        List<BiWrapper<UUID, UUID>> result = new ArrayList<>();

        jdbcTemplate.query(sql, rs -> {
            result.add(new BiWrapper<>(
                uuidConverter.convertEntity(rs.getString("user_id")),
                uuidConverter.convertEntity(rs.getString("list_item_id"))
            ));
        });

        return result;
    }
}
