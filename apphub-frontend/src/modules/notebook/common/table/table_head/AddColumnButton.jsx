import React from "react";
import Button from "../../../../../common/component/input/Button";
import { newColumn } from "../../../index/notebook_modules/opened_item/table/service/TableColumnCrudService";

const AddColumnButton = ({ id, label, callback }) => {
    return (
        <Button
            id={id}
            label={label}
            onclick={callback}
        />
    );
}

export default AddColumnButton;