import React from "react";
import PreLabeledInputField from "../../../../../../common/component/input/PreLabeledInputField";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";

const Color = ({ data, updateData, selectType, localizationHandler }) => {
    return (
        <div>
            <PreLabeledInputField
                label={data}
                input={
                    <InputField
                        type="color"
                        onchangeCallback={updateData}
                        value={data}
                    />
                }
            />

            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />
        </div>
    );
}

export default Color;