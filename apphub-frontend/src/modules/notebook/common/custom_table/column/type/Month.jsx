import React from "react";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";

const Month = ({ data, updateData, selectType, localizationHandler }) => {
    return (
        <div>
            <InputField
                type="month"
                onchangeCallback={updateData}
                value={data}
            />
            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />
        </div>
    );
}

export default Month;