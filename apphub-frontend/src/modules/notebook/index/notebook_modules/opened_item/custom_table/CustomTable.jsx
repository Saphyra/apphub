import React, { useEffect, useState } from "react";
import Endpoints from "../../../../../../common/js/dao/dao";
import Stream from "../../../../../../common/js/collection/Stream";
import TableHeadData from "../../../../common/table/table_head/TableHeadData";
import MapStream from "../../../../../../common/js/collection/MapStream";
import CustomTableColumnData from "../../../../common/custom_table/column/CustomTableColumnData";
import TableRowData from "../../../../common/table/row/TableRowData";
import ListItemTitle from "../../../../common/list_item_title/ListItemTitle";
import Button from "../../../../../../common/component/input/Button";
import ListItemType from "../../../../common/ListItemType";
import TableHead from "../../../../common/table/table_head/TableHead";
import CustomTableRow from "../../../../common/custom_table/CustomTableRow";
import Utils from "../../../../../../common/js/Utils";
import MoveDirection from "../../../../common/MoveDirection";
import "./custom_table.css";
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";

const CustomTable = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent, setConfirmationDialogData }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [tableHeads, setTableHeads] = useState([]);
    const [rows, setRows] = useState([]);

    useEffect(() => loadCustomTable(), [openedListItem]);

    //System
    const loadCustomTable = () => {
        const fetch = async () => {
            const response = await Endpoints.NOTEBOOK_GET_CUSTOM_TABLE.createRequest(null, { listItemId: openedListItem.id })
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

        const trows = new MapStream(tableColumnMapping)
            .toList((rowIndex, columns) => {
                const columnDataList = new Stream(columns)
                    .map(column => new CustomTableColumnData(
                        column.columnIndex,
                        column.type,
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
                <CustomTableRow
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

    const newRow = () => {
        const rowIndex = new Stream(rows)
            .map(row => row.rowIndex)
            .max()
            .orElse(0);

        const columns = new Stream(tableHeads)
            .map(tableHead => tableHead.columnIndex)
            .map(columnIndex => new CustomTableColumnData(columnIndex))
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

        const newTableHead = new TableHeadData(columnIndex + 1);
        const copy = new Stream(tableHeads)
            .add(newTableHead)
            .toList();
        setTableHeads(copy);

        new Stream(rows)
            .forEach(row => {
                const newColumn = new CustomTableColumnData(newTableHead.columnIndex);
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

    const discard = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-custom-table-discard-confirmation",
            localizationHandler.get("confirm-discard-title"),
            localizationHandler.get("confirm-discard-content"),
            [
                <Button
                    key="discard"
                    id="notebook-content-custom-table-discard-confirm-button"
                    label={localizationHandler.get("discard")}
                    onclick={() => {
                        setEditingEnabled(false);
                        loadCustomTable();
                        setConfirmationDialogData(null);
                    }}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-custom-table-discard-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const save = async () => {
        //TODO
    }

    return (
        <div id="notebook-content-custom-table" className="notebook-content notebook-content-view">
            <ListItemTitle
                id="notebook-content-custom-table-title"
                placeholder={localizationHandler.get("list-item-title")}
                value={title}
                setListItemTitle={setTitle}
                disabled={!editingEnabled}
                closeButton={
                    <Button
                        id="notebook-content-custom-table-close-button"
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
                        id="notebook-content-custom-table-edit-button"
                        label={localizationHandler.get("edit")}
                        onclick={() => setEditingEnabled(true)}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-custom-table-discard-button"
                        label={localizationHandler.get("discard")}
                        onclick={() => discard()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-custom-table-save-button"
                        label={localizationHandler.get("save")}
                        onclick={() => save()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-custom-table-new-row-button"
                        label={localizationHandler.get("new-row")}
                        onclick={() => newRow()}
                    />
                }

                {editingEnabled &&
                    <Button
                        id="notebook-content-custom-table-new-column-button"
                        label={localizationHandler.get("new-column")}
                        onclick={() => newColumn()}
                    />
                }
            </div>
        </div>
    );
}

export default CustomTable;