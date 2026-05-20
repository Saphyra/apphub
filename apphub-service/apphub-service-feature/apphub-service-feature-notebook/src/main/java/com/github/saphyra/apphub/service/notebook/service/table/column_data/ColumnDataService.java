package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;

import java.util.Optional;

public interface ColumnDataService {
    boolean canProcess(ColumnType type);

    /**
     * @return empty if {@link ColumnType} is EMPTY
     */
    Optional<String> serialize(Object data);

    Object deserialize(String data);

    <T> T deserialize(String data, Class<T> clazz);

    void validateData(Object data);
}
