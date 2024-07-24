import React from "react";
import ShowAndHide from "./show_and_hide/ShowAndHide";
import "./population_filtering.css";
import SortCitizens from "./sort/SortCitizens";

const PopulationFiltering = ({
    hiddenProperties,
    setHiddenProperties,
    citizenComparator,
    setCitizenComparator,
    hideSetting,
    updateHidden,
    orderSetting,
    updateOrder,
    planetId
}) => {
    return (
        <div id="skyxplore-game-population-filtering">
            <ShowAndHide
                hiddenProperties={hiddenProperties}
                setHiddenProperties={setHiddenProperties}
                hideSetting={hideSetting}
                updateHidden={updateHidden}
                planetId={planetId}
            />

            <SortCitizens
                citizenComparator={citizenComparator}
                setCitizenComparator={setCitizenComparator}
                orderSetting={orderSetting}
                updateOrder={updateOrder}
                planetId={planetId}
            />
        </div>
    );
}

export default PopulationFiltering;