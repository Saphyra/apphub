package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.feature.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class TextColumnDataService implements ColumnDataService {
    private static final Set<ColumnType> SUPPORTED_TYPES = EnumSet.of(
        ColumnType.TEXT,
        ColumnType.CHECKBOX,
        ColumnType.COLOR,
        ColumnType.DATE,
        ColumnType.TIME,
        ColumnType.DATE_TIME,
        ColumnType.MONTH
    );

    @Override
    public boolean canProcess(ColumnType type) {
        return SUPPORTED_TYPES.contains(type);
    }

    @Override
    public void validateData(Object data) {
        ValidationUtil.notNull(data, "data");
    }

    @Override
    public Optional<BiWrapper<String, Optional<UUID>>> serialize(Object data) {
        return Optional.of(new BiWrapper<>(data.toString(), Optional.empty()));
    }

    @Override
    public Object deserialize(String data) {
        return data;
    }
}
