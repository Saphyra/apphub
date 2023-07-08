import { findAllByTestId } from "@testing-library/react";
import React from "react";

const InputField = ({
    id,
    className,
    type,
    placeholder,
    onchangeCallback,
    value,
    onkeyupCallback,
    step,
    disabled = false,
    checked = false,
    autoFocus = false
}) => {
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
            onKeyUp={onkeyupCallback}
            disabled={disabled}
            checked={checked}
            autoFocus={autoFocus}
            step={step}
        />
    )
}

export default InputField;