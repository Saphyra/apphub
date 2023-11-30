package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.util.ContentBasedColumnTypeProxy;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Number;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//TODO unit test
class NumberColumnDataService extends ContentBasedColumnDataService {
    private final ObjectMapperWrapper objectMapperWrapper;

    public NumberColumnDataService(ContentDao contentDao, ContentBasedColumnTypeProxy proxy, ObjectMapperWrapper objectMapperWrapper) {
        super(ColumnType.NUMBER, contentDao, proxy);
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @Override
    protected String stringifyContent(Object data) {
        return objectMapperWrapper.writeValueAsString(data);
    }

    @Override
    public void validateData(Object data) {
        Number number = ValidationUtil.parse(data, (d) -> objectMapperWrapper.convertValue(d, Number.class), "numberData");
        ValidationUtil.notNull(number.getValue(), "number.value");
        ValidationUtil.atLeastExclusive(number.getStep(), 0d, "number.step");
    }
}
