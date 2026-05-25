package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FileColumnDataService implements ColumnDataService {
    private final ObjectMapper objectMapper;
    private final UuidConverter uuidConverter;

    @Override
    public boolean canProcess(ColumnType type) {
        return type.isFile();
    }

    @Override
    public Optional<String> serialize(Object data) {
        return Optional.of(objectMapper.writeValueAsString(data));
    }

    @Override
    public Object deserialize(String data) {
        return data;
    }

    @Override
    public <T> T deserialize(String data, Class<T> clazz) {
        if(clazz.equals(UUID.class)) {
            return (T) uuidConverter.convertEntity(data);
        }

        throw new UnsupportedOperationException("StoredFileId cannot be deserialized to "  + clazz.getName());
    }

    @Override
    public void validateData(Object data) {
        ValidationUtil.notNull(data, "storedFileId");
    }
}
