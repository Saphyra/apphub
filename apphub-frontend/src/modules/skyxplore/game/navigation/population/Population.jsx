import React, { useEffect, useState } from "react";
import localizationData from "./population_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Button from "../../../../../common/component/input/Button";
import Endpoints from "../../../../../common/js/dao/dao";
import Stream from "../../../../../common/js/collection/Stream";
import Citizen from "./citizen/Citizen";
import "./population.css";
import PopulationFiltering from "./filtering/PopulationFiltering";
import { ByNameCitizenComparator, fromComparatorData } from "./filtering/sort/CitizenComparator";
import Order from "./filtering/sort/Order";
import useConnectToWebSocket from "../../../../../common/hook/ws/WebSocketFacade";
import WebSocketEndpoint from "../../../../../common/hook/ws/WebSocketEndpoint";
import WebSocketEventName from "../../../../../common/hook/ws/WebSocketEventName";
import useLoadSetting, { SettingType } from "../../common/hook/Setting";
import Utils from "../../../../../common/js/Utils";

const Population = ({ footer, closePage, planetId }) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [population, setPopulation] = useState([]);
    const [hiddenProperties, setHiddenProperties] = useState([]);
    const [citizenComparator, setCitizenComparator] = useState(new ByNameCitizenComparator(Order.ASCENDING));
    const [hideSetting, setHideSetting] = useState(null);
    const [orderSetting, setOrderSetting] = useState(null);

    const updateHidden = (o) => {
        if (Utils.hasValue(o)) {
            setHiddenProperties(o.data);
        }

        setHideSetting(o);
    }

    const updateOrder = (o) => {
        if (Utils.hasValue(o)) {
            setCitizenComparator(fromComparatorData(o.data));
        }

        setOrderSetting(o);
    }

    useEffect(() => loadPopulation(), []);
    useLoadSetting(SettingType.POPULATION_HIDE, planetId, updateHidden);
    useLoadSetting(SettingType.POPULATION_ORDER, planetId, updateOrder);

    useConnectToWebSocket(
        WebSocketEndpoint.SKYXPLORE_GAME_POPULATION,
        sendMessage => {
            const event = {
                eventName: WebSocketEventName.SKYXPLORE_GAME_POPULATION_OPENED,
                payload: planetId
            };
            sendMessage(JSON.stringify(event));
        },
        lastEvent => {
            if (lastEvent.eventName === WebSocketEventName.SKYXPLORE_GAME_POPULATION_MODIFIED) {
                setPopulation(lastEvent.payload);
            }
        }
    );

    const loadPopulation = () => {
        const fetch = async () => {
            const response = await Endpoints.SKYXPLORE_PLANET_GET_POPULATION.createRequest(null, { planetId: planetId })
                .send();

            setPopulation(response);
        }
        fetch();
    }

    const getContent = () => {
        return new Stream(population)
            .sorted((a, b) => citizenComparator.compare(a, b))
            .map(citizen => <Citizen
                key={citizen.citizenId}
                citizen={citizen}
                hiddenProperties={hiddenProperties}
            />)
            .toList();
    }

    return (
        <div>
            <header id="skyxplore-game-population-header">
                <h1>{localizationHandler.get("title")}</h1>

                <Button
                    id="skyxplore-game-population-close-button"
                    className="skyxplore-game-window-close-button"
                    label="X"
                    onclick={closePage}
                />
            </header>

            <main id="skyxplore-game-population">
                <PopulationFiltering
                    hiddenProperties={hiddenProperties}
                    setHiddenProperties={setHiddenProperties}
                    citizenComparator={citizenComparator}
                    setCitizenComparator={setCitizenComparator}
                    hideSetting={hideSetting}
                    updateHidden={updateHidden}
                    orderSetting={orderSetting}
                    updateOrder={updateOrder}
                    planetId={planetId}
                />

                <div id="skyxplore-game-population-citizens">
                    {getContent()}
                </div>
            </main>

            {footer}
        </div>
    );
}

export default Population;