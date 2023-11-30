package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.util.ContentBasedColumnTypeProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//TODO unit test
class ColorColumnDataService extends ContentBasedColumnDataService {
    ColorColumnDataService(ContentDao contentDao, ContentBasedColumnTypeProxy proxy) {
        super(ColumnType.COLOR, contentDao, proxy);
    }

    @Override
    public void validateData(Object data) {
        String hexString = data.toString();
        ValidationUtil.length(hexString, 7, "colorValue");
        if (hexString.charAt(0) != '#') {
            throw ExceptionFactory.invalidParam("colorValue", "first character is not #");
        }

        ValidationUtil.parse(hexString, v -> Integer.parseInt(v.toString().substring(1), 16), "colorValue");
    }
}
