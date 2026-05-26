package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Range;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class RangeColumnDataService implements ColumnDataService {
    private final ObjectMapper objectMapper;

    @Override
    public boolean canProcess(ColumnType type) {
        return ColumnType.RANGE == type;
    }

    public Object deserialize(String data) {
        return objectMapper.readValue(data, Range.class);
    }

    @Override
    public void validateData(Object data) {
        Range range = ValidationUtil.parse(data, (d) -> objectMapper.convertValue(d, Range.class), "range");
        ValidationUtil.atLeastExclusive(range.getStep(), 0, "range.step");
        ValidationUtil.notNull(range.getMin(), "range.min");
        ValidationUtil.atLeast(range.getMax(), range.getMin(), "range.max");
        ValidationUtil.betweenInclusive(range.getValue(), range.getMin(), range.getMax(), "range.value");
    }

    @Override
    public Optional<BiWrapper<String, Optional<UUID>>> serialize(Object data) {
        return Optional.of(new BiWrapper<>(objectMapper.writeValueAsString(data), Optional.empty()));
    }
}
