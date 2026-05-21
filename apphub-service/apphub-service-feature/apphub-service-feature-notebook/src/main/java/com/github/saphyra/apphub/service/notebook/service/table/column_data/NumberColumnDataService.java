package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Number;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class NumberColumnDataService implements ColumnDataService {
    private final ObjectMapper objectMapper;

    @Override
    public boolean canProcess(ColumnType type) {
        return ColumnType.NUMBER == type;
    }

    @Override
    public Optional<String> serialize(Object data) {
        return Optional.of(objectMapper.writeValueAsString(data));
    }

    @Override
    public Object deserialize(String data) {
        return objectMapper.readValue(data, Number.class);
    }

    @Override
    public <T> T deserialize(String data, Class<T> clazz) {
        if (clazz.equals(Number.class)) {
            return (T) deserialize(data);
        }

        throw new UnsupportedOperationException("Number cannot be deserialized to " + clazz.getName());
    }

    @Override
    public void validateData(Object data) {
        Number number = ValidationUtil.parse(data, (d) -> objectMapper.convertValue(d, Number.class), "number");
        ValidationUtil.notNull(number.getValue(), "number.value");
        ValidationUtil.atLeastExclusive(number.getStep(), 0d, "number.step");
    }
}
