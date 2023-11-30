package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.util.ContentBasedColumnTypeProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//TODO unit test
class TextColumnDataService extends ContentBasedColumnDataService {
    public TextColumnDataService(ContentDao contentDao, ContentBasedColumnTypeProxy proxy) {
        super(ColumnType.TEXT, contentDao, proxy);
    }

    @Override
    public void validateData(Object data) {
        ValidationUtil.notNull(data, "textValue");
    }
}
