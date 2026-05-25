package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;

import java.util.Optional;
import java.util.UUID;

public interface ColumnDataService {
    boolean canProcess(ColumnType type);

    void validateData(Object data);

    /**
     * Handles the deletion of column's data
     */
    default void deleteData(String data) {
    }

    /**
     * Serializes the data to String so it can be stored i a {@link com.github.saphyra.apphub.service.notebook.dao.list_item.content.Content}
     *
     * @return the serialized data, and the generated storedFileId if file type
     */
    Optional<BiWrapper<String, Optional<UUID>>> serialize(Object data);

    /**
     * Deserializes the data String to an Object format
     */
    Object deserialize(String data);
}
