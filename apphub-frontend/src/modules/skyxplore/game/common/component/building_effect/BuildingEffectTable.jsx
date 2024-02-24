import React from "react";
import localizationData from "./building_effect_table_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";
import BuildingEffectTableHead from "./table_head/BuildingEffectTableHead";
import BuildingEffectTableBody from "./table_body/BuildingEffectTableBody";

const BuildingEffectTable = ({ id, className, surfaceType, itemData, currentLevel }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    return (
        <table id={id} className={className + " formatted-table"}>
            <BuildingEffectTableHead
                buildingType={itemData.buildingType}
                localizationHandler={localizationHandler}
                dataId={itemData.id}
            />

            <BuildingEffectTableBody
                itemData={itemData}
                surfaceType={surfaceType}
                localizationHandler={localizationHandler}
                currentLevel={currentLevel}
            />
        </table>
    );
}

export default BuildingEffectTable;