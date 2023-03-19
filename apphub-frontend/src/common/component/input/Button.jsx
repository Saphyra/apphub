import React from "react";

const Button = ({ label, onclick, disabled = false }) => {
    return (
        <button
            onClick={onclick}
            disabled={disabled}
        >
            {label}
        </button>
    );
}

export default Button;