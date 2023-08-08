import React from "react";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";

const Date = ({ data, updateData, selectType, localizationHandler }) => {
    return (
        <div>
            <InputField
                type="date"
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

export default Date;