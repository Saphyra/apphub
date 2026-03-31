import React from "react";

const Polyline = ({ id, className, points , style}) => {
    return (
        <polyline
            id={id}
            className={className}
            points={points}
            style={style}
        />
    );
}

export default Polyline;