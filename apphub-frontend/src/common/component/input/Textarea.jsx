import React from "react";

const Textarea = ({
    id,
    className,
    onchangeCallback,
    placeholder,
    value,
    disabled = false,
    onKeyUpCallback = () => { },
    onKeyDownCallback = () => { },
}) => {
    return (
        <textarea
            id={id}
            className={className}
            onChange={(e) => onchangeCallback(e.target.value)}
            placeholder={placeholder}
            value={value}
            disabled={disabled}
            onKeyUp={onKeyUpCallback}
            onKeyDown={onKeyDownCallback}
        />
    )
}

export default Textarea;