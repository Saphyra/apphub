import React, { useEffect, useState } from "react";
import localizationData from "./sort_citizens_localization.json";
import LocalizationHandler from "../../../../../../../common/js/LocalizationHandler";
import CitizenOrder from "./order/CitizenOrder";
import CitizenSortMethodSelector from "./method/CitizenSortMethodSelector";
import Order from "./Order";
import Button from "../../../../../../../common/component/input/Button";
import { SettingType } from "../../../../common/hook/Setting";
import { CitizenComparatorName } from "./CitizenComparator";
import { hasValue } from "../../../../../../../common/js/Utils";
import { SKYXPLORE_DATA_CREATE_SETTING, SKYXPLORE_DATA_DELETE_SETTING } from "../../../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";

const SortCitizens = ({
    citizenComparator,
    setCitizenComparator,
    orderSetting,
    updateOrder,
    planetId
}) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [order, setOrder] = useState(Order.ASCENDING);

    useEffect(() => setOrder(citizenComparator.order), [citizenComparator]);
    useEffect(() => setCitizenComparator(citizenComparator.withOrder(order)), [order]);

    const getDeleteSettingButton = () => {
        if (!hasValue(orderSetting)) {
            return;
        } else if (!hasValue(orderSetting.location)) {
            return <Button
                id="skyxplore-game-population-order-delete-global-default-button"
                label={localizationHandler.get("delete-global-default")}
                onclick={() => deleteSetting(null)}
            />
        } else {
            return <Button
                id="skyxplore-game-population-order-delete-planet-default-button"
                label={localizationHandler.get("delete-planet-default")}
                onclick={() => deleteSetting(planetId)}
            />
        }
    }

    const saveSetting = (location) => {
        const data = {
            order: citizenComparator.order.name,
            type: citizenComparator.name
        }

        switch (citizenComparator.name) {
            case CitizenComparatorName.BY_STAT:
                data.stat = citizenComparator.stat;
                break;
            case CitizenComparatorName.BY_SKILL:
                data.skill = citizenComparator.skill;
                break;
        }

        const payload = {
            location: location,
            type: SettingType.POPULATION_ORDER,
            data: data
        }

        SKYXPLORE_DATA_CREATE_SETTING.createRequest(payload)
            .send();
    }

    const deleteSetting = async (location) => {
        const payload = {
            location: location,
            type: SettingType.POPULATION_ORDER,
        }

        const response = await SKYXPLORE_DATA_DELETE_SETTING.createRequest(payload)
            .send();

        updateOrder(response.value);
    }

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

            <div className="skyxplore-game-population-default-buttons">
                <Button
                    id="skyxplore-game-population-order-save-planet-default-button"
                    label={localizationHandler.get("save-planet-default")}
                    onclick={() => saveSetting(planetId)}
                />
                <Button
                    id="skyxplore-game-population-order-save-global-default-button"
                    label={localizationHandler.get("save-global-default")}
                    onclick={() => saveSetting(null)}
                />

                {getDeleteSettingButton()}
            </div>
        </div>
    );
}

export default SortCitizens;