import React from "react";

const RadioButtonInput = ({ id, className, name, value, checked, onchangeCallback }) => {
    const onchange = (e) => {
        onchangeCallback(e.target.value);
    }

    return (
        <input
            id={id}
            className={className}
            type="radio"
            name={name}
            value={value}
            checked={checked}
            onChange={onchange}
        />
    );
}

export default RadioButtonInput

