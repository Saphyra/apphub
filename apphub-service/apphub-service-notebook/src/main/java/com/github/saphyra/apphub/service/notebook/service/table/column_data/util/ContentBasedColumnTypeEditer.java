package com.github.saphyra.apphub.service.notebook.service.table.column_data.util;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ContentBasedColumnTypeEditer {
    private final DimensionDao dimensionDao;
    private final ContentDao contentDao;

    void edit(UUID columnId, Integer columnIndex, String contentValue) {
        Dimension column = dimensionDao.findByIdValidated(columnId);
        column.setIndex(columnIndex);
        dimensionDao.save(column);

        Content content = contentDao.findByParentValidated(columnId);
        content.setContent(contentValue);
        contentDao.save(content);
    }
}
