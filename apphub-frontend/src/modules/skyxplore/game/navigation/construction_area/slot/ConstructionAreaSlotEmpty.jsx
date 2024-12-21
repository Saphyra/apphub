import React from "react";
import Button from "../../../../../../common/component/input/Button";
import NavigationHistoryItem from "../../NavigationHistoryItem";
import PageName from "../../PageName";

const ConstructionAreaSlotEmpty = ({ localizationHandler, openPage, constructionAreaId, buildingModuleCategory }) => {
    const openConstructModulePage = () => {
        openPage(new NavigationHistoryItem(PageName.CONSTRUCT_BUILDING_MODULE, { constructionAreaId: constructionAreaId, buildingModuleCategory: buildingModuleCategory }));
    }

    return (
        <div className="skyxplore-game-construction-area-slot skyxplore-game-construction-area-slot-empty">
            <Button
                className="skyxplore-game-construction-area-construct-module-button"
                label="+"
                title={localizationHandler.get("construct-building")}
                onclick={openConstructModulePage}
            />
        </div>
    );
}

export default ConstructionAreaSlotEmpty;