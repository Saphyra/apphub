import React from "react";
import Button from "../../../../../../common/component/input/Button";

const Empty = ({ selectType, editingEnabled, localizationHandler }) => {
    if (editingEnabled) {
        return (
            <Button
                className="notebook-custom-table-select-column-type-button"
                label={localizationHandler.get("select-column-type")}
                onclick={selectType}
            />
        );
    } else {
        return null;
    }
}

export default Empty;