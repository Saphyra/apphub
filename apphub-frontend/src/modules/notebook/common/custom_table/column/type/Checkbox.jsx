import React from "react";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";

const Checkbox = ({ checked, updateData, selectType, localizationHandler }) => {
    return (
        <div>
            <InputField
                type="checkbox"
                onchangeCallback={updateData}
                checked={Boolean(checked)}
            />

            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />
        </div>
    )
}

export default Checkbox;