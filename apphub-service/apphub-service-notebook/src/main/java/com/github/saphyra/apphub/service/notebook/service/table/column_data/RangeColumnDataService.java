package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.util.ContentBasedColumnTypeProxy;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Range;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//TODO unit test
class RangeColumnDataService extends ContentBasedColumnDataService {
    private final ObjectMapperWrapper objectMapperWrapper;

    public RangeColumnDataService(ContentDao contentDao, ContentBasedColumnTypeProxy proxy, ObjectMapperWrapper objectMapperWrapper) {
        super(ColumnType.RANGE, contentDao, proxy);
        this.objectMapperWrapper = objectMapperWrapper;
    }

    @Override
    protected String stringifyContent(Object data) {
        return objectMapperWrapper.writeValueAsString(data);
    }

    @Override
    public void validateData(Object data) {
        Range range = ValidationUtil.parse(data, (d) -> objectMapperWrapper.convertValue(d, Range.class), "rangeData");
        ValidationUtil.atLeastExclusive(range.getStep(), 0, "range.step");
        ValidationUtil.notNull(range.getMin(), "range.min");
        ValidationUtil.atLeast(range.getMax(), range.getMin(), "range.max");
        ValidationUtil.betweenInclusive(range.getValue(), range.getMin(), range.getMax(), "range.value");
    }
}
