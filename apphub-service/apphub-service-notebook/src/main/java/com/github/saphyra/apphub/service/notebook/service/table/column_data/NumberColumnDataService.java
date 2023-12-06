package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnProxy;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Number;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
class NumberColumnDataService extends ContentBasedColumnDataService {
    private final ObjectMapperWrapper objectMapperWrapper;

    NumberColumnDataService(ContentDao contentDao, ContentBasedColumnProxy proxy, ObjectMapperWrapper objectMapperWrapper) {
        super(ColumnType.NUMBER, contentDao, proxy);
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @Override
    protected String stringifyContent(Object data) {
        return objectMapperWrapper.writeValueAsString(data);
    }

    @Override
    public Object getData(UUID columnId) {
        String content = super.getData(columnId)
            .toString();
        return objectMapperWrapper.readValue(content, Number.class);
    }

    @Override
    public void validateData(Object data) {
        Number number = ValidationUtil.parse(data, (d) -> objectMapperWrapper.convertValue(d, Number.class), "number");
        ValidationUtil.notNull(number.getValue(), "number.value");
        ValidationUtil.atLeastExclusive(number.getStep(), 0d, "number.step");
    }
}
