import React from "react";
import ShowAndHide from "./show_and_hide/ShowAndHide";
import "./population_filtering.css";
import SortCitizens from "./sort/SortCitizens";

const PopulationFiltering = ({ hiddenProperties, setHiddenProperties, citizenComparator, setCitizenComparator }) => {
    return (
        <div id="skyxplore-game-population-filtering">
            <ShowAndHide
                hiddenProperties={hiddenProperties}
                setHiddenProperties={setHiddenProperties}
            />

            <SortCitizens
                citizenComparator={citizenComparator}
                setCitizenComparator={setCitizenComparator}
            />
        </div>
    );
}

export default PopulationFiltering;