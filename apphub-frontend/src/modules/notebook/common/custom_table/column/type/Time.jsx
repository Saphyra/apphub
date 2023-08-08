import React from "react";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";

const Time = ({  data, updateData, selectType, localizationHandler }) => {
    return (
        <div>
            <InputField
                type="time"
                onchangeCallback={updateData}
                value={data}
                step={1}
            />
            <Button
                className="notebook-custom-table-change-column-type-button"
                onclick={selectType}
                title={localizationHandler.get("change-column-type")}
            />
        </div>
    );
}

export default Time;