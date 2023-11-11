import React from "react";
import Button from "../../../../../../../common/component/input/Button";
import { newRow } from "../service/TableRowCrudService";

const AddRowButton = ({ indexRange, id, rows, tableHeads, setRows, checklist }) => {
    return (
        <tr>
            {checklist ? <td colSpan={2}></td> : <td></td>}
            <td colSpan={tableHeads.length}>
                <Button
                    id={id}
                    className={"notebook-content-table-add-row-button"}
                    label="+"
                    onclick={() => newRow(rows, tableHeads, setRows, indexRange)}
                />
            </td>
        </tr>
    );
}

export default AddRowButton;