import React from "react";
import Button from "../../../../../../../common/component/input/Button";
import { newColumn } from "../service/TableColumnCrudService";

const AddColumnButton = ({ tableHeads, setTableHeads, rows, setRows, id, label, indexRange }) => {
    return (
        <Button
            id={id}
            label={label}
            onclick={() => newColumn(tableHeads, setTableHeads, rows, setRows, indexRange)}
        />
    );
}

export default AddColumnButton;