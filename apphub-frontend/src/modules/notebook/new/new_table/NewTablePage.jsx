import React, { useEffect, useState } from "react";
import localizationData from "./new_table_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { useParams } from "react-router";
import sessionChecker from "../../../../common/js/SessionChecker";
import NotificationService from "../../../../common/js/notification/NotificationService";
import Header from "../../../../common/component/Header";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import { ToastContainer } from "react-toastify";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import Stream from "../../../../common/js/collection/Stream";
import TableHead from "../../common/table/table_head/TableHead";
import TableHeadData from "../../common/table/table_head/TableHeadData";
import "./new_table.css";
import MoveDirection from "../../common/MoveDirection";
import Utils from "../../../../common/js/Utils";
import TableColumnData from "../../common/table/row/column/TableColumnData";
import TableRowData from "../../common/table/row/TableRowData";
import TableRow from "../../common/table/row/TableRow";
import Endpoints from "../../../../common/js/dao/dao";
import validateListItemTitle from "../../common/validator/ListItemTitleValidator";
import validateTableHeadNames from "../../common/validator/TableHeadNameValidator";

const NewTablePage = ({ checklist }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [listItemTitle, setListItemTitle] = useState("");
    const [tableHeads, setTableHeads] = useState([new TableHeadData(0)]);
    const [rows, setRows] = useState([new TableRowData(0, [new TableColumnData(0)])]);

    useEffect(sessionChecker, []);
    useEffect(() => NotificationService.displayStoredMessages(), []);

    const create = async () => {
        const titleValidationResult = validateListItemTitle(listItemTitle);
        if (!titleValidationResult.valid) {
            NotificationService.showError(titleValidationResult.message);
            return;
        }

        const columnNames = new Stream(tableHeads)
            .sorted((a, b) => a.columnIndex - b.columnIndex)
            .map(tableHead => tableHead.content)
            .toList();

        const tableHeadNameValidationResult = validateTableHeadNames(columnNames);
        if (!tableHeadNameValidationResult.valid) {
            NotificationService.showError(tableHeadNameValidationResult.message);
            return;
        }

        if (checklist) {
            const payload = {
                title: listItemTitle,
                parent: parentId,
                columnNames: columnNames,
                rows: new Stream(rows)
                    .sorted((a, b) => a.rowIndex - b.rowIndex)
                    .map(row => {
                        return {
                            checked: row.checked,
                            columns: new Stream(row.columns)
                                .sorted((a, b) => a.columnIndex - b.columnIndex)
                                .map(column => column.content)
                                .toList()
                        }
                    })
                    .toList()
            }

            await Endpoints.NOTEBOOK_CREATE_CHECKLIST_TABLE.createRequest(payload)
                .send();
        } else {
            const payload = {
                title: listItemTitle,
                parent: parentId,
                columnNames: columnNames,
                columns: new Stream(rows)
                    .sorted((a, b) => a.rowIndex - b.rowIndex)
                    .map(row =>
                        new Stream(row.columns)
                            .sorted((a, b) => a.columnIndex - b.columnIndex)
                            .map(column => column.content)
                            .toList()
                    )
                    .toList()
            }

            await Endpoints.NOTEBOOK_CREATE_TABLE.createRequest(payload)
                .send();
        }

        window.location.href = Constants.NOTEBOOK_PAGE;
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

    //Content assembly
    const getTable = () => {
        return (
            <table id="notebook-new-table-content" className="formatted-table">
                <thead>
                    {getTableHeads()}
                </thead>

                <tbody>
                    {getTableRows()}
                </tbody>
            </table>
        )
    }

    const getTableHeads = () => {
        return (
            <tr>
                <th></th>
                {checklist && <th>{localizationHandler.get("checked")}</th>}

                {
                    new Stream(tableHeads)
                        .sorted((a, b) => a.columnIndex - b.columnIndex)
                        .map(tableHeadData =>
                            <TableHead
                                key={tableHeadData.tableHeadId}
                                localizationHandler={localizationHandler}
                                tableHeadData={tableHeadData}
                                updateTableHead={updateTableHead}
                                moveColumn={moveColumn}
                                removeColumn={removeColumn}
                            />
                        )
                        .toList()
                }
            </tr>
        )
    }

    const getTableRows = () => {
        return new Stream(rows)
            .sorted((a, b) => a.rowIndex - b.rowIndex)
            .map(row =>
                <TableRow
                    key={row.rowId}
                    rowData={row}
                    updateRow={updateRow}
                    updateChecked={updateRow}
                    moveRow={moveRow}
                    removeRow={removeRow}
                    checklist={checklist}
                />
            )
            .toList();
    }

    return (
        <div id="notebook-new-table" className="main-page">
            <Header label={localizationHandler.get("page-title")} />

            <main id="notebook-new-table-main">
                <ListItemTitle
                    inputId="notebook-new-table-title"
                    placeholder={localizationHandler.get("table-title")}
                    setListItemTitle={setListItemTitle}
                    value={listItemTitle}
                />

                <ParentSelector
                    parentId={parentId}
                    setParentId={setParentId}
                />

                <div id="notebook-new-table-content-wrapper">
                    {getTable()}
                </div>
            </main>

            <Footer
                leftButtons={[
                    <Button
                        key="new-column"
                        id="notebook-new-table-new-column-button"
                        label={localizationHandler.get("new-column")}
                        onclick={newColumn}
                    />,
                    <Button
                        key="new-row"
                        id="notebook-new-table-new-row-button"
                        label={localizationHandler.get("new-row")}
                        onclick={newRow}
                    />
                ]}

                centerButtons={
                    <Button
                        id="notebook-new-table-create-button"
                        label={localizationHandler.get("create")}
                        onclick={() => create()}
                    />
                }

                rightButtons={[
                    <Button
                        key="back-button"
                        id="notebook-new-checklist-back-button"
                        label={localizationHandler.get("back")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_NEW_PAGE + "/" + parent}
                    />,
                    <Button
                        key="home-button"
                        id="notebook-new-checklist-home-button"
                        label={localizationHandler.get("home")}
                        onclick={() => window.location.href = Constants.NOTEBOOK_PAGE}
                    />
                ]}
            />

            <ToastContainer />
        </div>
    )
}

export default NewTablePage;