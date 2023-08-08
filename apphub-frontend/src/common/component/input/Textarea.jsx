import React from "react";

const Textarea = ({ id, className, onchangeCallback, placeholder, value, disabled = false }) => {
    return (
        <textarea
            id={id}
            className={className}
            onChange={(e) => onchangeCallback(e.target.value)}
            placeholder={placeholder}
            value={value}
            disabled={disabled}
        />
    )
}

export default Textarea;