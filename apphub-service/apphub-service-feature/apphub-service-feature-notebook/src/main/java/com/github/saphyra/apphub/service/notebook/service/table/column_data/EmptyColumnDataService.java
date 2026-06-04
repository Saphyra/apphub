package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class EmptyColumnDataService implements ColumnDataService {
    @Override
    public boolean canProcess(ColumnType type) {
        return ColumnType.EMPTY.equals(type);
    }

    @Override
    public void validateData(Object data) {

    }

    @Override
    public Optional<BiWrapper<String, Optional<UUID>>> serialize(Object data) {
        return Optional.empty();
    }

    @Override
    public Object deserialize(String data) {
        return null;
    }
}
