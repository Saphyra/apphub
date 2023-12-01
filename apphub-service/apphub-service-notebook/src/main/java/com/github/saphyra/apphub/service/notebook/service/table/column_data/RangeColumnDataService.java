package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnProxy;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Range;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
class RangeColumnDataService extends ContentBasedColumnDataService {
    private final ObjectMapperWrapper objectMapperWrapper;

    RangeColumnDataService(ContentDao contentDao, ContentBasedColumnProxy proxy, ObjectMapperWrapper objectMapperWrapper) {
        super(ColumnType.RANGE, contentDao, proxy);
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
        return objectMapperWrapper.readValue(content, Range.class);
    }

    @Override
    public void validateData(Object data) {
        Range range = ValidationUtil.parse(data, (d) -> objectMapperWrapper.convertValue(d, Range.class), "range");
        ValidationUtil.atLeastExclusive(range.getStep(), 0, "range.step");
        ValidationUtil.notNull(range.getMin(), "range.min");
        ValidationUtil.atLeast(range.getMax(), range.getMin(), "range.max");
        ValidationUtil.betweenInclusive(range.getValue(), range.getMin(), range.getMax(), "range.value");
    }
}
