package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Number;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class NumberColumnDataService implements ColumnDataService {
    private final ObjectMapper objectMapper;

    @Override
    public boolean canProcess(ColumnType type) {
        return ColumnType.NUMBER == type;
    }

    @Override
    public void validateData(Object data) {
        Number number = ValidationUtil.parse(data, (d) -> objectMapper.convertValue(d, Number.class), "number");
        ValidationUtil.notNull(number.getValue(), "number.value");
        ValidationUtil.atLeastExclusive(number.getStep(), 0d, "number.step");
    }

    @Override
    public Optional<BiWrapper<String, Optional<UUID>>> serialize(Object data) {
        return Optional.of(new BiWrapper<>(objectMapper.writeValueAsString(data), Optional.empty()));
    }

    @Override
    public Object deserialize(String data) {
        return objectMapper.readValue(data, Number.class);
    }
}
