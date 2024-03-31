import Utils from "../../../../../common/js/Utils"
import Stream from "../../../../../common/js/collection/Stream"
import TableRow from "../../../common/table/row/TableRow"
import TableHead from "../../../common/table/table_head/TableHead"
import AddColumnButton from "../../../common/table/table_head/AddColumnButton"
import { moveColumn, newColumn, removeColumn } from "./NewTableColumnCrudService"
import { moveRow, newRow, removeRow } from "./NewTableRowCrudService"
import TableHeadIndexRange from "../../../common/table/table_head/TableHeadIndexRange"
import AddRowButton from "../../../common/table/row/AddRowButton"
import RowIndexRange from "../../../common/table/row/RowIndexRange"

const getTable = (checklist, localizationHandler, tableHeads, setTableHeads, rows, setRows, custom, addFile) => {
    return (
        <table id="notebook-new-table-content" className="formatted-table">
            <thead>
                {getTableHeads(checklist, localizationHandler, tableHeads, setTableHeads, rows, setRows, custom)}
            </thead>

            <tbody>
                <AddRowButton
                    id="notebook-new-table-add-row-to-start"
                    className={"notebook-new-table-add-row-button"}
                    tableHeads={tableHeads}
                    checklist={checklist}
                    callback={() => newRow(rows, tableHeads, setRows, RowIndexRange.MIN, custom)}
                />

                {getTableRows(rows, setRows, checklist, custom, addFile)}

                <AddRowButton
                    id="notebook-new-table-add-row-to-end"
                    className={"notebook-new-table-add-row-button"}
                    tableHeads={tableHeads}
                    checklist={checklist}
                    callback={() => newRow(rows, tableHeads, setRows, RowIndexRange.MAX, custom)}
                />
            </tbody>
        </table>
    )
}

const getTableHeads = (checklist, localizationHandler, tableHeads, setTableHeads, rows, setRows, custom) => {
    const updateTableHeads = () => {
        Utils.copyAndSet(tableHeads, setTableHeads);
    }

    const updateRows = () => {
        Utils.copyAndSet(rows, setRows);
    }

    return (
        <tr>
            <th>
                <AddColumnButton
                    id="notebook-new-table-add-column-to-start"
                    label="|<+"
                    callback={() => newColumn(tableHeads, setTableHeads, rows, setRows, TableHeadIndexRange.MIN, custom)}
                />

                <AddColumnButton
                    id="notebook-new-table-add-column-to-end"
                    label="+>|"
                    callback={() => newColumn(tableHeads, setTableHeads, rows, setRows, TableHeadIndexRange.MAX, custom)}
                />
            </th>
            {checklist && <th>{localizationHandler.get("checked")}</th>}

            {
                new Stream(tableHeads)
                    .sorted((a, b) => a.columnIndex - b.columnIndex)
                    .map(tableHeadData =>
                        <TableHead
                            key={tableHeadData.tableHeadId}
                            localizationHandler={localizationHandler}
                            tableHeadData={tableHeadData}
                            updateTableHead={updateTableHeads}
                            moveColumn={(tableHead, moveDirection) => moveColumn(
                                tableHead,
                                moveDirection,
                                tableHeads,
                                updateTableHeads,
                                rows,
                                updateRows
                            )}
                            removeColumn={(tableHead) => removeColumn(
                                tableHead,
                                tableHeads,
                                setTableHeads,
                                rows,
                                updateRows
                            )}
                        />
                    )
                    .toList()
            }
        </tr>
    )
}

const getTableRows = (rows, setRows, checklist, custom, addFile) => {
    const updateRows = () => {
        Utils.copyAndSet(rows, setRows);
    }

    return new Stream(rows)
        .sorted((a, b) => a.rowIndex - b.rowIndex)
        .map(row =>
            <TableRow
                key={row.rowId}
                rowData={row}
                updateRow={updateRows}
                updateChecked={updateRows}
                moveRow={(row, moveDirection) => moveRow(
                    row,
                    moveDirection,
                    rows,
                    updateRows
                )}
                removeRow={(row) => removeRow(
                    row,
                    rows,
                    setRows
                )}
                checklist={checklist}
                custom={custom}
                addFile={addFile}
            />
        )
        .toList();
}

export default getTable;