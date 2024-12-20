package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewCategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewLinkActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewOnlyTitleActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewTextActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.endpoints.NotebookEndpoints;
import com.github.saphyra.apphub.integration.structure.Link;
import com.github.saphyra.apphub.integration.structure.Number;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.view.notebook.ChecklistItem;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.CheckedTableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.LinkTableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.NumberTableColumn;
import com.github.saphyra.apphub.integration.structure.view.notebook.table.column.TableColumn;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

import static com.github.saphyra.apphub.integration.core.TestBase.OBJECT_MAPPER_WRAPPER;
import static java.util.Objects.isNull;

public class NotebookUtils {
    public static void newCategory(int serverPort, WebDriver driver, String title, String... parents) {
        NotebookActions.newListItem(serverPort, driver);
        NotebookNewListItemActions.selectListItemType(serverPort, driver, ListItemType.CATEGORY);

        selectParent(driver, parents);

        NewCategoryActions.fillTitle(driver, title);
        NewCategoryActions.submit(driver);

        waitForNotebookPageOpened(driver);
    }

    private static void selectParent(WebDriver driver, String[] parents) {
        if (isNull(parents)) {
            throw new UnsupportedOperationException("Up is not available yet");
        } else {
            for (String parent : parents) {
                ParentSelectorActions.selectParent(driver, parent);
            }
        }
    }

    public static void newLink(int serverPort, WebDriver driver, String title, String url) {
        NotebookActions.newListItem(serverPort, driver);
        NotebookNewListItemActions.selectListItemType(serverPort, driver, ListItemType.LINK);

        NewLinkActions.fillTitle(driver, title);
        NewLinkActions.fillUrl(driver, url);
        NewLinkActions.submit(driver);

        waitForNotebookPageOpened(driver);
    }

    public static void waitForNotebookPageOpened(WebDriver driver) {
        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(NotebookEndpoints.NOTEBOOK_PAGE))
            .assertTrue("Notebook page is not opened");
    }

    public static void newText(int serverPort, WebDriver driver, String title, String content, String... parents) {
        NotebookActions.newListItem(serverPort, driver);
        NotebookNewListItemActions.selectListItemType(serverPort, driver, ListItemType.TEXT);

        NewTextActions.fillTitle(driver, title);
        NewTextActions.fillContent(driver, content);
        selectParent(driver, parents);
        NewTextActions.submit(driver);

        waitForNotebookPageOpened(driver);
    }

    public static void newChecklist(int serverPort, WebDriver driver, String title, List<BiWrapper<String, Boolean>> items, String... parents) {
        NotebookActions.newListItem(serverPort, driver);
        NotebookNewListItemActions.selectListItemType(serverPort, driver, ListItemType.CHECKLIST);

        NewChecklistActions.fillTitle(driver, title);

        if (items.isEmpty()) {
            NewChecklistActions.getItems(driver)
                .get(0)
                .remove();
        } else {
            ChecklistItem firstItem = NewChecklistActions.getItems(driver)
                .get(0);
            firstItem.setValue(items.get(0).getEntity1());
            firstItem.setChecked(items.get(0).getEntity2());
        }

        for (int i = 1; i < items.size(); i++) {
            ChecklistItem checklistItem = NewChecklistActions.getItems(driver)
                .get(i);
            checklistItem.setValue(items.get(i).getEntity1());
            checklistItem.setChecked(items.get(i).getEntity2());
        }

        selectParent(driver, parents);
        NewChecklistActions.submit(driver);

        waitForNotebookPageOpened(driver);
    }

    public static void newTable(int serverPort, WebDriver driver, String title, List<String> tableHeads, List<List<String>> columns) {
        NotebookActions.newListItem(serverPort, driver);
        NotebookNewListItemActions.selectListItemType(serverPort, driver, ListItemType.TABLE);

        NewTableActions.fillTitle(driver, title);

        setTableHeads(driver, tableHeads);

        if (columns.isEmpty()) {
            NewTableActions.getRows(driver)
                .get(0)
                .remove();
        } else {
            List<TableColumn> firstColumns = NewTableActions.getRows(driver)
                .get(0)
                .getColumns();
            for (int i = 0; i < columns.get(0).size(); i++) {
                firstColumns.get(i)
                    .setValue(columns.get(0).get(i));
            }

            for (int rowIndex = 1; rowIndex < columns.size(); rowIndex++) {
                NewTableActions.addRowToEnd(driver);

                List<TableColumn> tableColumns = NewTableActions.getRows(driver)
                    .get(rowIndex)
                    .getColumns();
                for (int columnIndex = 0; columnIndex < columns.get(rowIndex).size(); columnIndex++) {
                    tableColumns.get(columnIndex)
                        .setValue(columns.get(rowIndex).get(columnIndex));
                }
            }
        }

        NewTableActions.submit(driver);

        waitForNotebookPageOpened(driver);
    }

    public static void newChecklistTable(int serverPort, WebDriver driver, String title, List<String> tableHeads, List<BiWrapper<Boolean, List<String>>> rows) {
        NotebookActions.newListItem(serverPort, driver);
        NotebookNewListItemActions.selectListItemType(serverPort, driver, ListItemType.CHECKLIST_TABLE);

        NewTableActions.fillTitle(driver, title);

        setTableHeads(driver, tableHeads);

        if (rows.isEmpty()) {
            NewTableActions.getRows(driver)
                .get(0)
                .remove();
        } else {
            List<TableColumn> firstColumns = NewTableActions.getRows(driver)
                .get(0)
                .getColumns();
            for (int i = 0; i < rows.get(0).getEntity2().size(); i++) {
                firstColumns.get(i)
                    .setValue(rows.get(0).getEntity2().get(i));
            }

            for (int rowIndex = 1; rowIndex < rows.size(); rowIndex++) {
                NewTableActions.addRowToEnd(driver);

                List<TableColumn> tableColumns = NewTableActions.getRows(driver)
                    .get(rowIndex)
                    .getColumns();
                for (int columnIndex = 0; columnIndex < rows.get(rowIndex).getEntity2().size(); columnIndex++) {
                    tableColumns.get(columnIndex)
                        .setValue(rows.get(rowIndex).getEntity2().get(columnIndex));
                }
            }
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            NewTableActions.getRows(driver)
                .get(rowIndex)
                .setChecked(rows.get(rowIndex).getEntity1());
        }

        NewTableActions.submit(driver);

        waitForNotebookPageOpened(driver);
    }

    private static void setTableHeads(WebDriver driver, List<String> tableHeads) {
        if (tableHeads.isEmpty()) {
            NewTableActions.getTableHeads(driver)
                .get(0)
                .remove();
        } else {
            NewTableActions.getTableHeads(driver)
                .get(0)
                .setValue(tableHeads.get(0));

            for (int i = 1; i < tableHeads.size(); i++) {
                NewTableActions.getTableHeads(driver)
                    .get(i)
                    .setValue(tableHeads.get(i));
            }
        }
    }

    public static void newOnlyTitle(int serverPort, WebDriver driver, String title, String... parents) {
        NotebookActions.newListItem(serverPort, driver);
        NotebookNewListItemActions.selectListItemType(serverPort, driver, ListItemType.ONLY_TITLE);

        Arrays.stream(parents)
            .forEach(parent -> ParentSelectorActions.selectParent(driver, parent));
        NewOnlyTitleActions.fillTitle(driver, title);
        NewOnlyTitleActions.submit(driver);

        waitForNotebookPageOpened(driver);
    }

    public static void newCustomTable(int serverPort, WebDriver driver, String title, List<String> tableHeads, List<List<BiWrapper<ColumnType, Object>>> rows) {
        NotebookActions.newListItem(serverPort, driver);
        NotebookNewListItemActions.selectListItemType(serverPort, driver, ListItemType.CUSTOM_TABLE);

        NewTableActions.fillTitle(driver, title);

        NewTableActions.getRows(driver)
            .get(0)
            .remove();
        NewTableActions.getTableHeads(driver)
            .get(0)
            .remove();

        if (!rows.isEmpty()) {
            for (int i = 0; i < rows.size(); i++) {
                NewTableActions.addRowToEnd(driver);
            }

            for (int i = 0; i < rows.get(0).size(); i++) {
                NewTableActions.addColumnToEnd(driver);
            }
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < rows.get(rowIndex).size(); columnIndex++) {
                setColumn(driver, rowIndex, columnIndex, rows.get(rowIndex).get(columnIndex));
            }
        }

        for (int columnIndex = 0; columnIndex < tableHeads.size(); columnIndex++) {
            NewTableActions.getTableHeads(driver)
                .get(columnIndex)
                .setValue(tableHeads.get(columnIndex));
        }

        NewTableActions.submit(driver);
        waitForNotebookPageOpened(driver);
    }

    private static void setColumn(WebDriver driver, int rowIndex, int columnIndex, BiWrapper<ColumnType, Object> column) {
        NewTableActions.setColumnType(driver, rowIndex, columnIndex, column.getEntity1());

        switch (column.getEntity1()) {
            case TEXT -> NewTableActions.getRows(driver)
                .get(rowIndex)
                .getColumns()
                .get(columnIndex)
                .setValue(column.getEntity2().toString());
            case NUMBER -> {
                NumberTableColumn numberColumn = NewTableActions.getRows(driver)
                    .get(rowIndex)
                    .getColumns()
                    .get(columnIndex)
                    .as(ColumnType.NUMBER);
                Number number = OBJECT_MAPPER_WRAPPER.convertValue(column.getEntity2(), Number.class);
                numberColumn.setStep(number.getStep().intValue());
                numberColumn.setValue(number.getValue().intValue());
            }
            case LINK -> {
                LinkTableColumn linkColumn = NewTableActions.getRows(driver)
                    .get(rowIndex)
                    .getColumns()
                    .get(columnIndex)
                    .as(ColumnType.LINK);

                Link link = OBJECT_MAPPER_WRAPPER.convertValue(column.getEntity2(), Link.class);

                linkColumn.setLabel(link.getLabel());
                linkColumn.setUrl(link.getUrl());
            }
            case CHECKBOX -> {
                CheckedTableColumn checkedColumn = NewTableActions.getRows(driver)
                    .get(rowIndex)
                    .getColumns()
                    .get(columnIndex)
                    .as(ColumnType.CHECKBOX);
                checkedColumn.setChecked(Boolean.parseBoolean(column.getEntity2().toString()));
            }
        }
    }
}
