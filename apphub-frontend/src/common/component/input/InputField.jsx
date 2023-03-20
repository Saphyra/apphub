import React from "react";

const InputField = ({ id, className, type, placeholder, onchangeCallback, value }) => {
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
            id={id}
            className={className}
            type={type}
            value={value}
            placeholder={placeholder}
            onChange={onchange}
        />
    )
}

export default InputField;