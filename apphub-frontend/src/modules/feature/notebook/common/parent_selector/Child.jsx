import React from "react";

const Child = ({ data, setParentId }) => {
    return (
        <div
            id={data.id}
            className="notebook-parent-selector-child button"
            onClick={() => setParentId(data.id)}
        >
            {data.title}
        </div>
    )
}

export default Child;