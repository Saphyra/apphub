import React, { useEffect, useState } from "react";
import Endpoints from "../../../../../../common/js/dao/dao";
import Stream from "../../../../../../common/js/collection/Stream";
import TableHeadData from "../../../../common/table/table_head/TableHeadData";
import MapStream from "../../../../../../common/js/collection/MapStream";
import TableColumnData from "../../../../common/table/row/column/TableColumnData";
import TableRowData from "../../../../common/table/row/TableRowData";
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
import ConfirmationDialogData from "../../../../../../common/component/confirmation_dialog/ConfirmationDialogData";
import Event from "../../../../../../common/js/event/Event";
import useHasFocus from "../../../../../../common/js/UseHasFocus";
import { useUpdateEffect } from "react-use";
import OpenedListItemHeader from "../OpenedListItemHeader";

const Table = ({ localizationHandler, openedListItem, setOpenedListItem, setLastEvent, checklist, setConfirmationDialogData }) => {
    const [editingEnabled, setEditingEnabled] = useState(false);
    const [parent, setParent] = useState(null);
    const [title, setTitle] = useState("");
    const [tableHeads, setTableHeads] = useState([]);
    const [rows, setRows] = useState([]);

    useEffect(() => loadTable(), [openedListItem]);

    const isInFocus = useHasFocus();
    useUpdateEffect(() => {
        if (isInFocus && !editingEnabled) {
            loadTable();
        }
    }, [isInFocus]);

    //System
    const loadTable = () => {
        const fetch = async () => {
            const endpoint = checklist ? Endpoints.NOTEBOOK_GET_CHECKLIST_TABLE : Endpoints.NOTEBOOK_GET_TABLE;

            const response = await endpoint.createRequest(null, { listItemId: openedListItem.id })
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
                    .map(column => new TableColumnData(
                        column.columnIndex,
                        column.content,
                        column.tableJoinId
                    ))
                    .toList();

                return new TableRowData(
                    Number(rowIndex),
                    columnDataList,
                    checklist ? response.rowStatus[rowIndex].checked : false,
                    checklist ? response.rowStatus[rowIndex].rowId : undefined
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
                    updateChecked={updateChecked}
                    removeRow={removeRow}
                    moveRow={moveRow}
                    editingEnabled={editingEnabled}
                    checklist={checklist}
                />
            )
            .toList();
    }

    //Operations
    const discard = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-table-discard-confirmation",
            localizationHandler.get("confirm-discard-title"),
            localizationHandler.get("confirm-discard-content"),
            [
                <Button
                    key="discard"
                    id="notebook-content-table-discard-confirm-button"
                    label={localizationHandler.get("discard")}
                    onclick={() => {
                        setEditingEnabled(false);
                        loadTable();
                        setConfirmationDialogData(null);
                    }}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-table-discard-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const save = async () => {
        const titleValidationResult = validateListItemTitle(title);
        if (!titleValidationResult.valid) {
            NotificationService.showError(titleValidationResult.message);
            return;
        }

        const columnNames = new Stream(tableHeads)
            .map(tableHead => tableHead.content)
            .peek(content => console.log(content))
            .toList();

        const tableHeadNameValidationResult = validateTableHeadNames(columnNames);
        if (!tableHeadNameValidationResult.valid) {
            NotificationService.showError(tableHeadNameValidationResult.message);
            return;
        }

        const tablePayload = {
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

        let response;
        if (checklist) {
            const payload = {
                table: tablePayload,
                rows: new Stream(rows)
                    .map(row => {
                        return {
                            rowId: row.rowId,
                            rowIndex: row.rowIndex,
                            checked: row.checked
                        }
                    })
                    .toList()
            };
            response = await Endpoints.NOTEBOOK_EDIT_CHECKLIST_TABLE.createRequest(payload, { listItemId: openedListItem.id })
                .send();

        } else {
            response = await Endpoints.NOTEBOOK_EDIT_TABLE.createRequest(tablePayload, { listItemId: openedListItem.id })
                .send();
        }


        setEditingEnabled(false);
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
        setDataFromResponse(response);
    }

    const confirmDeleteChcecked = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-table-delete-checked-confirmation",
            localizationHandler.get("confirm-delete-checked-title"),
            localizationHandler.get("confirm-delete-checked-content"),
            [
                <Button
                    key="delete"
                    id="notebook-content-table-delete-checked-confirm-button"
                    label={localizationHandler.get("delete-checked")}
                    onclick={deleteChecked}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-table-delete-checked-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const convertToChecklistTableDialog = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "notebook-content-table-conversion-confirmation",
            localizationHandler.get("confirm-table-conversion-title"),
            localizationHandler.get("confirm-table-conversion-content"),
            [
                <Button
                    key="delete"
                    id="notebook-content-table-conversion-confirm-button"
                    label={localizationHandler.get("confirm-table-conversion")}
                    onclick={convertToChecklistTable}
                />,
                <Button
                    key="cancel"
                    id="notebook-content-table-conversion-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ));
    }

    const convertToChecklistTable = async () => {
        await Endpoints.NOTEBOOK_CONVERT_TABLE_TO_CHECKLIST_TABLE.createRequest(null, { listItemId: openedListItem.id })
            .send();

        setConfirmationDialogData(null);
        setLastEvent(new Event(EventName.NOTEBOOK_LIST_ITEM_MODIFIED));
        setOpenedListItem({ id: openedListItem.id, type: ListItemType.CHECKLIST_TABLE });
    }

    const deleteChecked = async () => {
        const response = await Endpoints.NOTEBOOK_CHECKLIST_TABLE_DELETE_CHECKED.createRequest(null, { listItemId: openedListItem.id })
            .send();

        setDataFromResponse(response);
        setConfirmationDialogData(null);
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

    const updateChecked = (row) => {
        if (!editingEnabled) {
            Endpoints.NOTEBOOK_UPDATE_CHECKLIST_TABLE_ROW_STATUS.createRequest({ value: row.checked }, { rowId: row.rowId })
                .send();
        }

        updateRow();
    }

    const close = () => {
        if (editingEnabled) {
            setConfirmationDialogData(new ConfirmationDialogData(
                "notebook-content-table-close-confirmation",
                localizationHandler.get("confirm-close-title"),
                localizationHandler.get("confirm-close-content"),
                [
                    <Button
                        key="close"
                        id="notebook-content-table-close-confirm-button"
                        label={localizationHandler.get("close")}
                        onclick={doClose}
                    />,
                    <Button
                        key="cancel"
                        id="notebook-content-table-close-cancel-button"
                        label={localizationHandler.get("cancel")}
                        onclick={() => setConfirmationDialogData(null)}
                    />
                ]
            ));
        } else {
            doClose();
        }
    }

    const doClose = () => {
        setOpenedListItem({ id: parent, type: ListItemType.CATEGORY })
        setConfirmationDialogData(null);
    }

    return (
        <div id="notebook-content-table" className="notebook-content notebook-content-view">
            <OpenedListItemHeader
                localizationHandler={localizationHandler}
                title={title}
                setTitle={setTitle}
                editingEnabled={editingEnabled}
                close={close}
            />

            <div id="notebook-content-table-content" className="notebook-content-view-main">
                <table id="notebook-content-table-content-table" className="formatted-table content-selectable">
                    <thead>
                        <tr>
                            {editingEnabled && <th></th>}
                            {checklist && <th>{localizationHandler.get("checked")}</th>}
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

                {!editingEnabled && !checklist &&
                    <Button
                        id="notebook-content-table-convert-button"
                        label={localizationHandler.get("convert-to-checklist-table")}
                        onclick={() => convertToChecklistTableDialog()}
                    />
                }

                {checklist && !editingEnabled &&
                    <Button
                        id="notebook-content-table-delete-checked-button"
                        label={localizationHandler.get("delete-checked")}
                        onclick={() => confirmDeleteChcecked()}
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