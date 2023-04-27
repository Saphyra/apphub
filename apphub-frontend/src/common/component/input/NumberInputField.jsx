import React from "react";

const NumberInputField = ({ id, className, value, min, max, step = 1, onchange, disabled = false }) => {
    return (
        <input
            id={id}
            className={className}
            type="number"
            value={value}
            min={min}
            max={max}
            step={step}
            onChange={onchange}
            disabled={disabled}
        />
    );
}

export default NumberInputField;