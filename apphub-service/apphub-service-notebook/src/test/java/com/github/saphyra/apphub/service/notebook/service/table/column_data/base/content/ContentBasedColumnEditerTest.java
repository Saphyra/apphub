package com.github.saphyra.apphub.service.notebook.service.table.column_data.base.content;

import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ContentBasedColumnEditerTest {
    private static final UUID COLUMN_ID = UUID.randomUUID();
    private static final Integer COLUMN_INDEX = 3456;
    private static final String CONTENT = "content";

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ContentDao contentDao;

    @InjectMocks
    private ContentBasedColumnEditer underTest;

    @Mock
    private Dimension column;

    @Mock
    private Content content;

    @Test
    void edit() {
        given(dimensionDao.findByIdValidated(COLUMN_ID)).willReturn(column);
        given(contentDao.findByParentValidated(COLUMN_ID)).willReturn(content);

        underTest.edit(COLUMN_ID, COLUMN_INDEX, CONTENT);

        then(column).should().setIndex(COLUMN_INDEX);
        then(dimensionDao).should().save(column);

        then(content).should().setContent(CONTENT);
        then(contentDao).should().save(content);
    }
}