import React from "react";

const ProductionTableHead = ({localizationHandler }) => {
    return (
        <thead>
            <tr>
                <th colSpan={2}>{localizationHandler.get("produces")}</th>
            </tr>
            <tr>
                <th>{localizationHandler.get("commodity")}</th>
                <th>{localizationHandler.get("components")}</th>
            </tr>
        </thead>
    );
}

export default ProductionTableHead;