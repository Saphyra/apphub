import React from "react";
import { replaceTabsWithSpaces } from "./TabReplacer";

const Textarea = ({
    id,
    className = "",
    onchangeCallback,
    placeholder,
    value,
    disabled = false,
    onKeyUpCallback = () => { },
    onKeyDownCallback = () => { },
    reference = () => { },
    style,
    rows,
    spacesInsteadOfTab = false,
    autoResize = false
}) => {
    const onKeyDown = spacesInsteadOfTab ?
        (e) => {
            replaceTabsWithSpaces(e);
            onKeyDownCallback(e);
        }
        : onKeyDownCallback;

    const onKeyUp = autoResize ?
        (e) => {
            e.target.style.height = "auto";
            e.target.style.height = e.target.scrollHeight + 6 + "px";

            onKeyUpCallback(e);
        }
        : onKeyUpCallback;

    return (
        <textarea
            ref={r => reference(r)}
            id={id}
            className={className + (autoResize ? " resizable" : "")}
            onChange={(e) => onchangeCallback(e.target.value)}
            placeholder={placeholder}
            value={value}
            disabled={disabled}
            onKeyUp={onKeyUp}
            onKeyDown={onKeyDown}
            style={style}
            rows={rows}
        />
    )
}

export default Textarea;