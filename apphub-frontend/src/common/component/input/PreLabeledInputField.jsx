import React from "react";

const PreLabeledInputField = ({ label, input }) => {
    return (
        <label>
            <span>{label}</span>
            <span> </span>
            {input}
        </label>
    );
}

export default PreLabeledInputField;