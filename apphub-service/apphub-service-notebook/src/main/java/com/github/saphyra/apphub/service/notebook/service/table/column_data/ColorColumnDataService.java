package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class ColorColumnDataService extends ContentBasedColumnDataService {
    ColorColumnDataService(ContentDao contentDao, ContentBasedColumnProxy proxy) {
        super(ColumnType.COLOR, contentDao, proxy);
    }

    @Override
    public void validateData(Object data) {
        if(Constants.EMPTY_STRING.equals(data)){
            return;
        }

        ValidationUtil.notNull(data, "color");

        String hexString = data.toString();
        ValidationUtil.length(hexString, 7, "color");
        if (hexString.charAt(0) != '#') {
            throw ExceptionFactory.invalidParam("color", "first character is not #");
        }

        ValidationUtil.parse(hexString, v -> Integer.parseInt(v.toString().substring(1), 16), "color");
    }
}
