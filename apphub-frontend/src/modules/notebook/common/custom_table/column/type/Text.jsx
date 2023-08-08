import React from "react";
import InputField from "../../../../../../common/component/input/InputField";
import Button from "../../../../../../common/component/input/Button";

const Text = ({ content, updateContent, editingEnabled, selectType, localizationHandler }) => {
    if (editingEnabled) {
        return (
            <div>
                <InputField
                    className="noteabook-custom-table-column-input"
                    type="text"
                    onchangeCallback={updateContent}
                    value={content}
                />
                <Button
                    className="notebook-custom-table-change-column-type-button"
                    onclick={selectType}
                    title={localizationHandler.get("change-column-type")}
                />
            </div>
        );
    } else {
        return content
    }
}

export default Text;