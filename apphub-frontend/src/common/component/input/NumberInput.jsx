import React from "react";

const NumberInput = ({
    id,
    type = "number",
    className,
    placeholder,
    onchangeCallback = () => { },
    value,
    onkeyupCallback = () => { },
    min,
    max = 2_147_483_647,
    step = 1,
    disabled = false,
    autoFocus = false
}) => {
    const onchange = (e) => {
        if (onchangeCallback) {
            if (!isNaN(e.target.value)) {
                const number = Number(e.target.value);
                onchangeCallback(number);
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
            autoFocus={autoFocus}
            step={step}
            min={min}
            max={max}
        />
    )
}

export default NumberInput;