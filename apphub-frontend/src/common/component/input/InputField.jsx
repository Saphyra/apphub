import React from "react";
import Utils from "../../js/Utils";

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
    onfocusOutCallback = () => { },
    onclickCallback = () => { },
    inputRef,
    style
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
            value={value == null ? "" : value}
            placeholder={placeholder}
            onChange={onchange}
            onKeyUp={onkeyupCallback}
            disabled={disabled}
            checked={checked}
            autoFocus={autoFocus}
            step={step}
            onBlur={onfocusOutCallback}
            onClick={onclickCallback}
            ref={inputRef}
            style={style}
        />
    )
}

export default InputField;