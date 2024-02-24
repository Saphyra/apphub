package com.github.saphyra.apphub.service.notebook.service.table.column_data;

import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnDataService;
import com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content.ContentBasedColumnProxy;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
class LinkColumnDataService extends ContentBasedColumnDataService {
    private final ObjectMapperWrapper objectMapperWrapper;

    public LinkColumnDataService(ContentDao contentDao, ContentBasedColumnProxy proxy, ObjectMapperWrapper objectMapperWrapper) {
        super(ColumnType.LINK, contentDao, proxy);
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
        return objectMapperWrapper.readValue(content, Link.class);
    }

    @Override
    public void validateData(Object data) {
        Link link = ValidationUtil.parse(data, (d) -> objectMapperWrapper.convertValue(d, Link.class), "link");
        ValidationUtil.notBlank(link.getLabel(), "link.label");
        ValidationUtil.notNull(link.getUrl(), "link.url");
    }
}
