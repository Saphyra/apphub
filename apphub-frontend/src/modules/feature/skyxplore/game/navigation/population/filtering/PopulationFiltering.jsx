import "./population_filtering.css";
import ShowAndHide from "./show_and_hide/ShowAndHide";
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
    planetId,
    setDisplaySpinner
}) => {
    return (
        <div id="skyxplore-game-population-filtering">
            <ShowAndHide
                hiddenProperties={hiddenProperties}
                setHiddenProperties={setHiddenProperties}
                hideSetting={hideSetting}
                updateHidden={updateHidden}
                planetId={planetId}
                setDisplaySpinner={setDisplaySpinner}
            />

            <SortCitizens
                citizenComparator={citizenComparator}
                setCitizenComparator={setCitizenComparator}
                orderSetting={orderSetting}
                updateOrder={updateOrder}
                planetId={planetId}
                setDisplaySpinner={setDisplaySpinner}
            />
        </div>
    );
}

export default PopulationFiltering;