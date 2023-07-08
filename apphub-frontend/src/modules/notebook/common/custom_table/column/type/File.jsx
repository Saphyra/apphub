import React from "react";
import FileInput from "../../../../../../common/component/input/FileInput";
import Button from "../../../../../../common/component/input/Button";

const File = ({ updateData, selectType, localizationHandler }) => {
    return (
        <div className="nowrap">
            <FileInput
                onchangeCallback={updateData}
            />

            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />
        </div>
    );
}

export default File;