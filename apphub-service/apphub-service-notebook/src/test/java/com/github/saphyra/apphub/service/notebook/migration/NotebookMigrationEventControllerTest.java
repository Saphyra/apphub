package com.github.saphyra.apphub.service.notebook.migration;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.ColumnType;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.security.access_token.AccessTokenProvider;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDao;
import com.github.saphyra.apphub.service.notebook.dao.column_type.ColumnTypeDto;
import com.github.saphyra.apphub.service.notebook.dao.content.Content;
import com.github.saphyra.apphub.service.notebook.dao.content.ContentDao;
import com.github.saphyra.apphub.service.notebook.dao.dimension.Dimension;
import com.github.saphyra.apphub.service.notebook.dao.dimension.DimensionDao;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.service.notebook.service.table.dto.Link;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class NotebookMigrationEventControllerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final UUID ROW_ID = UUID.randomUUID();
    private static final UUID LINK_COLUMN_ID = UUID.randomUUID();
    private static final UUID OTHER_COLUMN_ID = UUID.randomUUID();
    private static final String ORIGINAL_CONTENT = "original-content";
    private static final String NEW_CONTENT = "new-content";

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private ErrorReporterService errorReporterService;

    @Mock
    private DimensionDao dimensionDao;

    @Mock
    private ContentDao contentDao;

    @Mock
    private ColumnTypeDao columnTypeDao;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private NotebookMigrationEventController underTest;

    @Mock
    private Dimension row;

    @Mock
    private Dimension linkColumn;

    @Mock
    private Dimension otherColumn;

    @Mock
    private ColumnTypeDto linkColumnType;

    @Mock
    private ColumnTypeDto otherColumnType;

    @Mock
    private Content content;

    @Test
    void getRequests() {
        assertThat(underTest.getRequests()).hasSize(1);
    }

    @Test
    void migration() {
        given(listItemDao.getByListItemTypeUnencrypted(ListItemType.CUSTOM_TABLE)).willReturn(List.of(new BiWrapper<>(USER_ID, LIST_ITEM_ID)));
        given(dimensionDao.getByExternalReference(LIST_ITEM_ID)).willReturn(List.of(row));
        given(row.getDimensionId()).willReturn(ROW_ID);
        given(dimensionDao.getByExternalReference(ROW_ID)).willReturn(List.of(linkColumn, otherColumn));
        given(linkColumn.getDimensionId()).willReturn(LINK_COLUMN_ID);
        given(otherColumn.getDimensionId()).willReturn(OTHER_COLUMN_ID);
        given(columnTypeDao.findByIdValidated(LINK_COLUMN_ID)).willReturn(linkColumnType);
        given(columnTypeDao.findByIdValidated(OTHER_COLUMN_ID)).willReturn(otherColumnType);
        given(linkColumnType.getType()).willReturn(ColumnType.LINK);
        given(otherColumnType.getType()).willReturn(ColumnType.TEXT);
        given(contentDao.findByParentValidated(LINK_COLUMN_ID)).willReturn(content);
        given(content.getContent()).willReturn(ORIGINAL_CONTENT);
        given(objectMapperWrapper.writeValueAsString(Link.builder().url(ORIGINAL_CONTENT).label(ORIGINAL_CONTENT).build())).willReturn(NEW_CONTENT);

        underTest.migration();

        then(accessTokenProvider).should().set(AccessTokenHeader.builder().userId(USER_ID).build());
        then(accessTokenProvider).should().clear();
        then(content).should().setContent(NEW_CONTENT);
        then(contentDao).should().save(content);
    }
}