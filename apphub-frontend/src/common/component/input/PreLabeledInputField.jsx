import React from "react";

const PreLabeledInputField = ({ label, input, className}) => {
    return (
        <label className={className}>
            <span>{label}</span>
            <span> </span>
            {input}
        </label>
    );
}

export default PreLabeledInputField;