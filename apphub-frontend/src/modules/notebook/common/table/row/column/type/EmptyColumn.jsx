import React from "react";
import Button from "../../../../../../../common/component/input/Button";

const EmptyColumn = ({ editingEnabled, selectType, localizationHandler }) => {
    if (editingEnabled) {
        return (
            <td className={"table-column editable notebook-table-column-type-empty"}>
                <div>
                    <Button
                        className="notebook-table-change-column-type-button"
                        onclick={selectType}
                        title={localizationHandler.get("change-column-type")}
                    />
                </div>
            </td>
        )
    } else {
        return (
            <td ></td>
        );
    }
}

export default EmptyColumn;