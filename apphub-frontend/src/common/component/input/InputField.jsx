import React from "react";

const InputField = ({
    id,
    className,
    type = "text",
    placeholder,
    onchangeCallback,
    value,
    onkeyupCallback,
    step,
    disabled = false,
    checked = false,
    autoFocus = false,
    onfocusOutCallback = () => {},
    onclickCallback = () => {}
}) => {
    const onchange = (e) => {
        if (onchangeCallback) {
            switch (type.toLowerCase()) {
                case "checkbox":
                case "radio":
                    onchangeCallback(e.target.checked, e);
                    break;
                default:
                    onchangeCallback(e.target.value, e);
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
            onBlur={onfocusOutCallback}
            onClick={onclickCallback}
        />
    )
}

export default InputField;