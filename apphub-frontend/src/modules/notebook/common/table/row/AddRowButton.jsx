import React from "react";
import Button from "../../../../../common/component/input/Button";

const AddRowButton = ({ id, className, checklist, tableHeads, callback }) => {
    return (
        <tr>
            {checklist ? <td colSpan={2}></td> : <td></td>}
            <td colSpan={tableHeads.length}>
                <Button
                    id={id}
                    className={className}
                    label="+"
                    onclick={callback}
                />
            </td>
        </tr>
    );
}

export default AddRowButton;