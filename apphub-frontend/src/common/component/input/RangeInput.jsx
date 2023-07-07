import React from "react";

const RangeInput = ({
    id,
    className,
    placeholder,
    onchangeCallback,
    value,
    onkeyupCallback,
    min,
    max,
    step = 1,
    disabled = false,
    autoFocus = false
}) => {
    const onchange = (e) => {
        if (onchangeCallback) {
            onchangeCallback(Number(e.target.value));
        }
    }

    return (
        <input
            id={id}
            className={className}
            type="range"
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

export default RangeInput;