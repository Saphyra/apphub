package com.github.saphyra.apphub.service.notebook.service.table.query;

import com.github.saphyra.apphub.api.notebook.model.ListItemType;
import com.github.saphyra.apphub.api.notebook.model.table.TableHeadModel;
import com.github.saphyra.apphub.api.notebook.model.table.TableResponse;
import com.github.saphyra.apphub.api.notebook.model.table.TableRowModel;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItem;
import com.github.saphyra.apphub.service.notebook.dao.list_item.ListItemDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TableQueryServiceTest {
    private static final UUID LIST_ITEM_ID = UUID.randomUUID();
    private static final String TITLE = "title";
    private static final UUID PARENT = UUID.randomUUID();

    @Mock
    private ListItemDao listItemDao;

    @Mock
    private TableHeadQueryService tableHeadQueryService;

    @Mock
    private TableRowQueryService tableRowQueryService;

    @InjectMocks
    private TableQueryService underTest;

    @Mock
    private ListItem listItem;

    @Mock
    private TableHeadModel tableHeadModel;

    @Mock
    private TableRowModel tableRowModel;

    @ParameterizedTest
    @MethodSource("nonTableListItems")
    void unsupportedListItemTypes(ListItemType listItemType) {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(listItemType);

        Throwable ex = catchThrowable(() -> underTest.getTable(LIST_ITEM_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.LIST_ITEM_NOT_FOUND);
    }

    @Test
    void getTable() {
        given(listItemDao.findByIdValidated(LIST_ITEM_ID)).willReturn(listItem);
        given(listItem.getType()).willReturn(ListItemType.TABLE);
        given(listItem.getTitle()).willReturn(TITLE);
        given(listItem.getParent()).willReturn(PARENT);
        given(tableHeadQueryService.getTableHeads(LIST_ITEM_ID)).willReturn(List.of(tableHeadModel));
        given(tableRowQueryService.getRows(LIST_ITEM_ID)).willReturn(List.of(tableRowModel));

        TableResponse result = underTest.getTable(LIST_ITEM_ID);

        assertThat(result.getTitle()).isEqualTo(TITLE);
        assertThat(result.getParent()).isEqualTo(PARENT);
        assertThat(result.getTableHeads()).containsExactly(tableHeadModel);
        assertThat(result.getRows()).containsExactly(tableRowModel);
    }

    private static Stream<Arguments> nonTableListItems() {
        return Stream.of(
            Arguments.of(ListItemType.CATEGORY),
            Arguments.of(ListItemType.TEXT),
            Arguments.of(ListItemType.CHECKLIST),
            Arguments.of(ListItemType.LINK),
            Arguments.of(ListItemType.ONLY_TITLE),
            Arguments.of(ListItemType.IMAGE),
            Arguments.of(ListItemType.FILE)
        );
    }
}