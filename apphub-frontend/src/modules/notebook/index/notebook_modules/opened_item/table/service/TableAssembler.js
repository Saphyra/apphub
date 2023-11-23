import Utils from "../../../../../../../common/js/Utils";
import Stream from "../../../../../../../common/js/collection/Stream";
import TableRow from "../../../../../common/table/row/TableRow";
import TableHead from "../../../../../common/table/table_head/TableHead";
import { moveColumn, removeColumn } from "./TableColumnCrudService";
import { updateChecked } from "./TableDao";
import { moveRow, removeRow } from "./TableRowCrudService";

export const getTableHeads = (tableHeads, localizationHandler, editingEnabled, setTableHeads, rows, setRows) => {
    return new Stream(tableHeads)
        .sorted((a, b) => a.columnIndex - b.columnIndex)
        .map(tableHead =>
            <TableHead
                key={tableHead.tableHeadId}
                localizationHandler={localizationHandler}
                tableHeadData={tableHead}
                updateTableHead={() => Utils.copyAndSet(tableHeads, setTableHeads)}
                moveColumn={(tableHead, moveDirection) => moveColumn(tableHead, moveDirection, tableHeads, setTableHeads, rows, setRows)}
                removeColumn={tableHead => removeColumn(tableHead, tableHeads, setTableHeads, rows, setRows)}
                editingEnabled={editingEnabled}
            />
        )
        .toList();
}

export const getTableRows = (rows, checklist, editingEnabled, setRows, custom) => {
    return new Stream(rows)
        .sorted((a, b) => a.rowIndex - b.rowIndex)
        .map(row =>
            <TableRow
                key={row.rowId}
                rowData={row}
                updateRow={() => Utils.copyAndSet(rows, setRows)}
                updateChecked={row => updateChecked(row, rows, setRows, editingEnabled)}
                removeRow={row => removeRow(row, rows, setRows)}
                moveRow={(row, moveDirection) => moveRow(row, moveDirection, rows, setRows)}
                editingEnabled={editingEnabled}
                checklist={checklist}
                custom={custom}
            />
        )
        .toList();
}