import React from "react";

const Button = ({ id, className, label, onclick, disabled = false, title}) => {
    const eventHandler = (e) => {
        e.stopPropagation();
        onclick(e);
    }

    return (
        <button
            id={id}
            className={className}
            onClick={eventHandler}
            disabled={disabled}
            title={title}
        >
            {label}
        </button>
    );
}

export default Button;