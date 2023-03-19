import React from "react";

const InputField = ({ type, placeholder, onchangeCallback, value }) => {
    const onchange = (e) => {
        if (onchangeCallback) {
            switch (type.toLowerCase()) {
                case "checkbox":
                case "radio":
                    onchangeCallback(e.target.checked);
                    break;
                default:
                    onchangeCallback(e.target.value);
            }
        }
    }

    return (
        <input
            type={type}
            value={value}
            placeholder={placeholder}
            onChange={onchange}
        />
    )
}

export default InputField;