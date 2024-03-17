import React from "react";
import Stream from "../../js/collection/Stream";

const SelectInput = ({ id, className, value, options = [], onchangeCallback, disabled = false }) => {
    const getOptions = () => {
        return new Stream(options)
            .map((option, index) =>
                <option
                    key={index}
                    value={option.value}
                >
                    {option.label}
                </option>
            )
            .toList();
    }

    const onchange = (e) => {
        onchangeCallback(e.target.value, e);
    }

    return (
        <select
            id={id}
            className={className}
            onChange={onchange}
            value={value}
            disabled={disabled}
        >
            {getOptions()}
        </select>
    );
}

export const SelectOption = class {
    constructor(label, value) {
        this.label = label;
        this.value = value;
    }
}

export default SelectInput;