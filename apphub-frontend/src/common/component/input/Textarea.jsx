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
    reference = () =>{},
    style,
    rows
}) => {
    return (
        <textarea
            ref={r => reference(r)}
            id={id}
            className={className}
            onChange={(e) => onchangeCallback(e.target.value)}
            placeholder={placeholder}
            value={value}
            disabled={disabled}
            onKeyUp={onKeyUpCallback}
            onKeyDown={onKeyDownCallback}
            style={style}
            rows={rows}
        />
    )
}

export default Textarea;