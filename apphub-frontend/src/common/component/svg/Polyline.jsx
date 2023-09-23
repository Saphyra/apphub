import React from "react";

const Polyline = ({ id, className, points }) => {
    return (
        <polyline
            id={id}
            className={className}
            points={points}
        />
    );
}

export default Polyline;