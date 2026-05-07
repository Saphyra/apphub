import React from "react";

const StorageTableHead = ({ localizationHandler }) => {
    return (
        <thead>
            <tr>
                <th colSpan={2} >{localizationHandler.get("stores")}</th>
            </tr>
        </thead>
    );
}

export default StorageTableHead;