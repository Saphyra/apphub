import React from "react";

const LabelWrappedInputField = ({ preLabel, postLabel, inputField }) => {
    return (
        <label>
            <span>{preLabel}</span>
            <span> </span>
            {inputField}
            <span> </span>
            <span>{postLabel}</span>
        </label>
    );
}

export default LabelWrappedInputField;