import React from "react";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";

const DateTime = ({ data, updateData, selectType, localizationHandler }) => {
    console.log(data)
    return (
        <div>
            <InputField
                type="datetime-local"
                onchangeCallback={updateData}
                value={data}
                step="1"
            />
            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />
        </div>
    );
}

export default DateTime;