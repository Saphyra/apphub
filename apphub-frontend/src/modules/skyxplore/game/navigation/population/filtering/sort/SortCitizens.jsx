import React, { useEffect, useState } from "react";
import localizationData from "./sort_citizens_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import CitizenOrder from "./order/CitizenOrder";
import CitizenSortMethodSelector from "./method/CitizenSortMethodSelector";
import Order from "./Order";

const SortCitizens = ({ citizenComparator, setCitizenComparator }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [order, setOrder] = useState(Order.ASCENDING);

    useEffect(() => setCitizenComparator(citizenComparator.withOrder(order)), [order]);

    return (
        <div className="skyxplore-game-population-filtering-panel">
            <div className="skyxplore-game-population-filtering-panel-title">
                {localizationHandler.get("title")}
            </div>

            <CitizenOrder
                order={order}
                setOrder={setOrder}
            />

            <CitizenSortMethodSelector
                order={order}
                citizenComparator={citizenComparator}
                setCitizenComparator={setCitizenComparator}
            />
        </div>
    );
}

export default SortCitizens;