import React from "react";

const Footer = ({ leftButtons = [], centerButtons = [], rightButtons = [] }) => {
    return (
        <footer>
            <span className="float-left">
                {leftButtons}
            </span>

            {centerButtons}

            <span className="float-right">
                {rightButtons}
            </span>
        </footer>
    );
}

export default Footer;