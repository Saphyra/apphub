import React, { useEffect, useState } from "react";
import Endpoints from "../../../../../../common/js/dao/dao";
import Stream from "../../../../../../common/js/collection/Stream";
import TableHeadData from "../../../../common/table/table_head/TableHeadData";
import MapStream from "../../../../../../common/js/collection/MapStream";
import TableColumnData from "../../../../common/table/row/column/TableColumnData";
import TableRowData from "../../../../common/table/row/TableRowData";
import ListItemTitle from "../../../../common/list_item_title/ListItemTitle";
import Button from "../../../../../../common/component/input/Button";
import ListItemType from "../../../../common/ListItemType";
import TableHead from "../../../../common/table/table_head/TableHead";
import "./table.css";
import TableRow from "../../../../common/table/row/TableRow";
import Utils from "../../../../../../common/js/Utils";
import MoveDirection from "../../../../common/MoveDirection";
import validateListItemTitle from "../../../../common/validator/ListItemTitleValidator";
import NotificationService from "../../../../../../common/js/notification/NotificationService";
import validateTableHeadNames from "../../../../common/validator/TableHeadNameValidator";
import EventName from "../../../../../../common/js/event/EventName";

const Table = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [tableHeads, setTableHeads] = useState([]);
    const [rows, setRows] = useState([]);

    useEffect(() => loadTable(), []);

    //System
    const loadTable = () => {
        const fetch = async () => {
            const response = await Endpoints.NOTEBOOK_GET_TABLE.createRequest(null, { listItemId: openedListItem.id })
                .send();

            setDataFromResponse(response);
        }
        fetch();
    }

    const setDataFromResponse = (response) => {
        setTitle(response.title);
        setParent(response.parent);
        const theads = new Stream(response.tableHeads)
            .map(data => new TableHeadData(
                data.columnIndex,
                data.content,
                data.tableHeadId
            ))
            .toList();
        setTableHeads(theads);

        const tableColumnMapping = {};
        new Stream(response.tableColumns)
            .forEach(tableColumn => {
                if (!tableColumnMapping[tableColumn.rowIndex]) {
                    tableColumnMapping[tableColumn.rowIndex] = [];
                }

                tableColumnMapping[tableColumn.rowIndex].push(tableColumn);
            });

        console.log(tableColumnMapping);

        const trows = new MapStream(tableColumnMapping)
            .toList((rowIndex, columns) => {
                const columnDataList = new Stream(columns)
                    .map(column => new TableColumnData(
                        column.columnIndex,
                        column.content,
                        column.tableJoinId
                    ))
                    .toList();

                return new TableRowData(
                    Number(rowIndex),
                    columnDataList
                );
            });
        setRows(trows);
    }

    const updateTableHead = () => {
        const copy = new Stream(tableHeads)
            .toList();
        setTableHeads(copy);
    }

    const updateRow = () => {
        const copy = new Stream(rows)
            .toList();
        setRows(copy);
    }

    //Assemble
    const getTableHeads = () => {
        return new Stream(tableHeads)
            .sorted((a, b) => a.columnIndex - b.columnIndex)
            .map(tableHead =>
                <TableHead
                    key={tableHead.tableHeadId}
                    localizationHandler={localizationHandler}
                    tableHeadData={tableHead}
                    updateTableHead={updateTableHead}
                    moveColumn={moveColumn}
                    removeColumn={removeColumn}
                    editingEnabled={editingEnabled}
                />
            )
            .toList();
    }

    const getTableRows = () => {
        return new Stream(rows)
            .sorted((a, b) => a.rowIndex - b.rowIndex)
            .map(row =>
                <TableRow
                    key={row.rowId}
                    rowData={row}
                    updateRow={updateRow}
                    removeRow={removeRow}
                    moveRow={moveRow}
                    editingEnabled={editingEnabled}
                />
            )
            .toList();
    }

    //Operations
    const discard = () => {
        setEditingEnabled(false);
        loadTable();
    }

    const save = async () => {
        const titleValidationResult = validateListItemTitle(title);
        if (!titleValidationResult.valid) {
            NotificationService.showError(titleValidationResult.message);
            return;
        }

        const columnNames = new Stream(tableHeads)
            .map(tableHead => tableHead.content)
            .toList();

        const tableHeadNameValidationResult = validateTableHeadNames(columnNames);
        if (!tableHeadNameValidationResult.valid) {
            NotificationService.showError(tableHeadNameValidationResult.message);
            return;
        }

        const payload = {
            title: title,
            tableHeads: new Stream(tableHeads)
                .sorted((a, b) => a.columnIndex - b.columnIndex)
                .map(tableHead => {
                    return {
                        tableHeadId: tableHead.tableHeadId,
                        columnIndex: tableHead.columnIndex,
                        columnName: tableHead.content
                    }
                })
                .toList(),
            columns: new Stream(rows)
                .flatMap(row =>
                    new Stream(row.columns)
                        .map(column => {
                            return {
                                tableJoinId: column.columnId,
                                rowIndex: row.rowIndex,
                                columnIndex: column.columnIndex,
                                content: column.content
                            }
                        })
                )
                .toList()
        }

        const response = await Endpoints.NOTEBOOK_EDIT_TABLE.createRequest(payload, { listItemId: openedListItem.id })
            .send();

        setEditingEnabled(false);
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
        setDataFromResponse(response);
    }

    const newRow = () => {
        const rowIndex = new Stream(rows)
            .map(row => row.rowIndex)
            .max()
            .orElse(0);

        const columns = new Stream(tableHeads)
            .map(tableHead => tableHead.columnIndex)
            .map(columnIndex => new TableColumnData(columnIndex))
            .toList();

        const newRow = new TableRowData(rowIndex + 1, columns);

        const copy = new Stream(rows)
            .add(newRow)
            .toList();

        setRows(copy);
    }

    const newColumn = () => {
        const columnIndex = new Stream(tableHeads)
            .map(tableHead => tableHead.columnIndex)
            .max()
            .orElse(0);

        const newTableHead = new TableHeadData(columnIndex + 1, "", Utils.generateRandomId());
        const copy = new Stream(tableHeads)
            .add(newTableHead)
            .toList();
        setTableHeads(copy);

        new Stream(rows)
            .forEach(row => {
                const newColumn = new TableColumnData(newTableHead.columnIndex);
                row.columns.push(newColumn);
            });

        updateRow();
    }

    const moveColumn = (tableHead, moveDirection) => {
        const orderedItems = new Stream(tableHeads)
            .sorted((a, b) => a.columnIndex - b.columnIndex)
            .toList();

        const tableHeadIndex = orderedItems.indexOf(tableHead);

        let otherTableHeadIndex;
        switch (moveDirection) {
            case MoveDirection.LEFT:
                otherTableHeadIndex = tableHeadIndex - 1;
                break;
            case MoveDirection.RIGHT:
                otherTableHeadIndex = tableHeadIndex + 1;
                break;
            default:
                Utils.throwException("IllegalArgument", "Unknown MoveDirection: " + moveDirection);
        }

        const otherTableHead = orderedItems[otherTableHeadIndex];

        if (!otherTableHead) {
            //Item is at the most left/right of the list and cannot be moved further.
            return;
        }

        const originalColumnIndex = tableHead.columnIndex;
        const newColumnIndex = otherTableHead.columnIndex;

        tableHead.columnIndex = newColumnIndex;
        otherTableHead.columnIndex = originalColumnIndex;

        updateTableHead();

        new Stream(rows)
            .forEach(row => {
                const originalColumn = new Stream(row.columns)
                    .filter(column => column.columnIndex === originalColumnIndex)
                    .findFirst()
                    .orElseThrow("IllegalState", "Column not found with columnIndex " + originalColumnIndex);

                const otherColumn = new Stream(row.columns)
                    .filter(column => column.columnIndex === newColumnIndex)
                    .findFirst()
                    .orElseThrow("IllegalState", "Column not found with columnIndex " + newColumnIndex);

                originalColumn.columnIndex = newColumnIndex;
                otherColumn.columnIndex = originalColumnIndex;
            });
        updateRow();
    }

    const removeColumn = (tableHead) => {
        const copy = new Stream(tableHeads)
            .remove(i => i === tableHead)
            .toList();
        setTableHeads(copy);

        new Stream(rows)
            .forEach(row => {
                const columns = new Stream(row.columns)
                    .remove(column => column.columnIndex === tableHead.columnIndex)
                    .toList();
                row.columns = columns;
            });

        updateRow();
    }

    const moveRow = (row, moveDirection) => {
        const orderedItems = new Stream(rows)
            .sorted((a, b) => a.rowIndex - b.rowIndex)
            .toList();

        const rowPosition = orderedItems.indexOf(row);

        let otherRowPosition;
        switch (moveDirection) {
            case MoveDirection.UP:
                otherRowPosition = rowPosition - 1;
                break;
            case MoveDirection.DOWN:
                otherRowPosition = rowPosition + 1;
                break;
            default:
                Utils.throwException("IllegalArgument", "Unknown MoveDirection: " + moveDirection);
        }

        const otherRow = orderedItems[otherRowPosition];

        if (!otherRow) {
            //Item is at the top/bottom of the list and cannot be moved further.
            return;
        }

        const originalRowIndex = row.rowIndex;
        const newRowIndex = otherRow.rowIndex;

        row.rowIndex = newRowIndex;
        otherRow.rowIndex = originalRowIndex;

        updateRow();
    }

    const removeRow = (row) => {
        const copy = new Stream(rows)
            .remove(r => r === row)
            .toList();
        setRows(copy);
    }

    return (
        <div id="notebook-content-table" className="notebook-content notebook-content-view">
            <ListItemTitle
                id="notebook-content-table-title"
                placeholder={localizationHandler.get("list-item-title")}
                value={title}
                setListItemTitle={setTitle}
                disabled={!editingEnabled}
                closeButton={
                    <Button
                        id="notebook-content-text-close-button"
                        className="notebook-close-button"
                        label="X"
                        onclick={() => setOpenedListItem({ id: parent, type: ListItemType.CATEGORY })}
                    />
                }
            />

            <div id="notebook-content-checklist-content" className="notebook-content-view-main">
                <table id="notebook-content-table-content" className="formatted-table">
                    <thead>
                        <tr>
                            {editingEnabled && <th></th>}
                            {getTableHeads()}
                        </tr>
                    </thead>

                    <tbody>
                        {getTableRows()}
                    </tbody>
                </table>
            </div>

            <div className="notebook-content-buttons">
                {!editingEnabled &&
                    <Button
                        id="notebook-content-table-edit-button"
                        label={localizationHandler.get("edit")}
                        onclick={() => setEditingEnabled(true)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-table-discard-button"
                        label={localizationHandler.get("discard")}
                        onclick={() => discard()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-table-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-table-new-row-button"
                        label={localizationHandler.get("new-row")}
                        onclick={() => newRow()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-table-new-column-button"
                        label={localizationHandler.get("new-column")}
                        onclick={() => newColumn()}
                    />
                }
            </div>
        </div>
    );
}

export default Table;