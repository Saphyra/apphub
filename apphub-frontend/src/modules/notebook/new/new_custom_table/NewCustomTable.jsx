import React, { useState } from "react";
import localizationData from "./new_custom_table_localization.json";
import LocalizationHandler from "../../../../common/js/LocalizationHandler";
import { useParams } from "react-router";
import TableHeadData from "../../common/table/table_head/TableHeadData";
import Header from "../../../../common/component/Header";
import ListItemTitle from "../../common/list_item_title/ListItemTitle";
import ParentSelector from "../../common/parent_selector/ParentSelector";
import Footer from "../../../../common/component/Footer";
import Button from "../../../../common/component/input/Button";
import Constants from "../../../../common/js/Constants";
import { ToastContainer } from "react-toastify";
import "./new_custom_table.css";
import TableRowData from "../../common/table/row/TableRowData";
import Stream from "../../../../common/js/collection/Stream";
import TableHead from "../../common/table/table_head/TableHead";
import MoveDirection from "../../common/MoveDirection";
import Utils from "../../../../common/js/Utils";
import CustomTableRow from "../../common/custom_table/CustomTableRow";
import CustomTableColumnData from "../../common/custom_table/column/CustomTableColumnData";
import CustomTableColumnTypeSelector from "../../common/custom_table/column_type_selector/CustomTableColumnTypeSelector";
import CustomTableColumnType from "../../common/custom_table/CustomTableColumnType";

const NewCustomTable = () => {
    const localizationHandler = new LocalizationHandler(localizationData);
    document.title = localizationHandler.get("title");

    const { parent } = useParams();
    const [parentId, setParentId] = useState(parent === "null" ? null : parent);
    const [listItemTitle, setListItemTitle] = useState("");
    const [tableHeads, setTableHeads] = useState([new TableHeadData(0)]);
    const [rows, setRows] = useState([new TableRowData(0, [new CustomTableColumnData(0)])]);
    const [columnTypeSelectorData, setColumnTypeSelectorData] = useState(null);

    //Operations
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

    const newRow = () => {
        const rowIndex = new Stream(rows)
            .map(row => row.rowIndex)
            .max()
            .orElse(0);

        const previousRowColumns = new Stream(rows)
            .sorted((a, b) => b.rowIndex - a.rowIndex)
            .findFirst()
            .map(row => row.columns)
            .orElse([]);

        const columns = new Stream(tableHeads)
            .map(tableHead => tableHead.columnIndex)
            .toMapStream(columnIndex => columnIndex, columnIndex => getColumnType(Number(columnIndex), previousRowColumns))
            .toList((columnIndex, columnType) => new CustomTableColumnData(Number(columnIndex), columnType, getDefaultDataForColumnType(columnType)))

        const newRow = new TableRowData(rowIndex + 1, columns);

        const copy = new Stream(rows)
            .add(newRow)
            .toList();

        setRows(copy);
    }

    const getColumnType = (columnIndex, previousRowColumns) => {
        return new Stream(previousRowColumns)
            .filter(column => column.columnIndex === columnIndex)
            .findFirst()
            .map(column => column.type)
            .orElseGet(() => {
                console.log("Previous column not found with columnIndex " + columnIndex, previousRowColumns);
                return CustomTableColumnType.EMPTY;
            });
    }

    const create = () => {
        //TODO
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

    const setColumnType = (rowIndex, columnIndex, columnType) => {
        const column = new Stream(rows)
            .filter((row => row.rowIndex === rowIndex))
            .flatMap(row => new Stream(row.columns))
            .filter(column => column.columnIndex === columnIndex)
            .findFirst()
            .orElseThrow("IllegalArgument", "No column found with rowIndex " + rowIndex + " and columnIndex " + columnIndex);

        column.type = columnType;
        column.data = getDefaultDataForColumnType(columnType);

        updateRow();
        setColumnTypeSelectorData(null);
    }

    const getDefaultDataForColumnType = (columnType) => {
        switch (columnType) {
            case CustomTableColumnType.NUMBER:
                return {
                    value: 0,
                    step: 1
                }
            case CustomTableColumnType.RANGE:
                return {
                    value: 0,
                    step: 1,
                    min: -10,
                    max: 10
                }
            case CustomTableColumnType.TEXT:
            case CustomTableColumnType.LINK:
            case CustomTableColumnType.DATE:
            case CustomTableColumnType.TIME:
            case CustomTableColumnType.DATE_TIME:
            case CustomTableColumnType.MONTH:
                return "";
            case CustomTableColumnType.CHECKBOX:
                return false;
            case CustomTableColumnType.COLOR:
                return "#000000";
            default:
                return null;
        }
    }

    //Data
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

    //Render
    const getTable = () => {
        return (
            <table id="notebook-new-custom-table-content" className="formatted-table">
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
                <CustomTableRow
                    key={row.rowId}
                    rowData={row}
                    updateRow={updateRow}
                    removeRow={removeRow}
                    moveRow={moveRow}
                    setColumnTypeSelectorData={setColumnTypeSelectorData}
                />
            )
            .toList();
    }

    return (
        <div id="notebook-new-custom-table" className="main-page">
            {!columnTypeSelectorData &&
                <div>
                    <Header label={localizationHandler.get("page-title")} />

                    <main id="notebook-new-custom-table-main">
                        <ListItemTitle
                            inputId="notebook-new-custom-table-title"
                            placeholder={localizationHandler.get("table-title")}
                            setListItemTitle={setListItemTitle}
                            value={listItemTitle}
                        />

                        <ParentSelector
                            parentId={parentId}
                            setParentId={setParentId}
                        />

                        <div id="notebook-new-custom-table-content-wrapper">
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
                </div>
            }

            {columnTypeSelectorData &&
                <CustomTableColumnTypeSelector
                    columnTypeSelectorData={columnTypeSelectorData}
                    setColumnTypeSelectorData={setColumnTypeSelectorData}
                    setColumnType={setColumnType}
                />
            }

            <ToastContainer />
        </div>
    );
}

export default NewCustomTable;