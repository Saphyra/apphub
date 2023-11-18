import Utils from "../../../../../common/js/Utils"
import Stream from "../../../../../common/js/collection/Stream"
import TableRow from "../../../common/table/row/TableRow"
import TableHead from "../../../common/table/table_head/TableHead"
import { moveColumn, removeColumn } from "./NewTableColumnCrudService"
import { moveRow, removeRow } from "./NewTableRowCrudService"

const getTable = (checklist, localizationHandler, tableHeads, setTableHeads, rows, setRows, custom) => {
    return (
        <table id="notebook-new-table-content" className="formatted-table">
            <thead>
                {getTableHeads(checklist, localizationHandler, tableHeads, setTableHeads, rows, setRows)}
            </thead>

            <tbody>
                {getTableRows(rows, setRows, checklist, custom)}
            </tbody>
        </table>
    )
}

const getTableHeads = (checklist, localizationHandler, tableHeads, setTableHeads, rows, setRows) => {
    const updateTableHeads = () => {
        Utils.copyAndSet(tableHeads, setTableHeads);
    }

    const updateRows = () => {
        Utils.copyAndSet(rows, setRows);
    }

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

const getTableRows = (rows, setRows, checklist, custom) => {
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
            />
        )
        .toList();
}

export default getTable;