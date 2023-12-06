package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
class DateTimeColumnDataService extends ContentBasedColumnDataService {
    DateTimeColumnDataService(ContentDao contentDao, ContentBasedColumnProxy proxy) {
        super(ColumnType.DATE_TIME, contentDao, proxy);
    }

    @Override
    public void validateData(Object data) {
        if (Constants.EMPTY_STRING.equals(data)) {
            return;
        }
        ValidationUtil.parse(data, o -> LocalDateTime.parse(o.toString()), "dateTime");
    }
}
