import React from "react";

const PreLabeledInputField = ({id, label, input, className, labelId}) => {
    return (
        <label id={id} className={className}>
            <span id={labelId}>{label}</span>
            <span> </span>
            {input}
        </label>
    );
}

export default PreLabeledInputField;