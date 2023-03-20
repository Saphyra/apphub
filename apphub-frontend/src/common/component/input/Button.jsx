import React from "react";

const Button = ({ id, className, label, onclick, disabled = false }) => {
    return (
        <button
            id={id}
            className={className}
            onClick={onclick}
            disabled={disabled}
        >
            {label}
        </button>
    );
}

export default Button;