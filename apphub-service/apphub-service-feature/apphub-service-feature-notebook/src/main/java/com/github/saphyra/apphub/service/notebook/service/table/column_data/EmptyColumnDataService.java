package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class EmptyColumnDataService implements ColumnDataService {
    @Override
    public boolean canProcess(ColumnType type) {
        return ColumnType.EMPTY.equals(type);
    }

    @Override
    public Optional<String> serialize(Object data) {
        return Optional.empty();
    }

    @Override
    public Object deserialize(String data) {
        throw new UnsupportedOperationException("Nothing to deserialize");
    }

    @Override
    public <T> T deserialize(String data, Class<T> clazz) {
        throw new UnsupportedOperationException("Nothing to deserialize");
    }

    @Override
    public void validateData(Object data) {

    }
}
