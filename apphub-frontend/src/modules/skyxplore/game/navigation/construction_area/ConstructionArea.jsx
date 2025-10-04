import React, { useState } from "react";
import localizationData from "./construction_area_localization.json";
import LocalizationHandler from "../../../../../common/js/LocalizationHandler";
import Button from "../../../../../common/component/input/Button";
import constructionAreaLocalizationData from "../../common/localization/construction_area_localization.json";
import useCache from "../../../../../common/hook/Cache";
import { SKYXPLORE_GET_ITEM_DATA } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";
import MapStream from "../../../../../common/js/collection/MapStream";
import { hasValue } from "../../../../../common/js/Utils";
import buildingModuleCategoryLocalizationData from "../../common/localization/building_module_category_localization.json";
import ConstructionAreaSlots from "./slot/ConstructionAreaSlots";
import useLoader from "../../../../../common/hook/Loader";
import { SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_GET_BUILDING_MODULES, SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreGameEndpoints";
import Stream from "../../../../../common/js/collection/Stream";
import WebSocketEndpoint from "../../../../../common/hook/ws/WebSocketEndpoint";
import WebSocketEventName from "../../../../../common/hook/ws/WebSocketEventName";
import useConnectToWebSocket from "../../../../../common/hook/ws/WebSocketFacade";
import "./construction_area.css";
import ConfirmationDialogData from "../../../../../common/component/confirmation_dialog/ConfirmationDialogData";

const ConstructionArea = ({ openPage, closePage, footer, constructionArea, setConfirmationDialogData }) => {
    const localizationHandler = new LocalizationHandler(localizationData);
    const constructionAreaLocalizationHandler = new LocalizationHandler(constructionAreaLocalizationData);
    const buildingModuleCategoryLocalizationHandler = new LocalizationHandler(buildingModuleCategoryLocalizationData);

    const [buildingModules, setBuildingModules] = useState([]);
    const [constructionAreaData, setConstructionAreaData] = useState(null);

    useCache(
        constructionArea.dataId,
        SKYXPLORE_GET_ITEM_DATA.createRequest(null, { dataId: constructionArea.dataId }),
        setConstructionAreaData
    );
    useLoader({
        request: SKYXPLORE_PLANET_SURFACE_CONSTRUCTION_AREA_GET_BUILDING_MODULES.createRequest(null, { constructionAreaId: constructionArea.constructionAreaId }),
        mapper: setBuildingModules
    });

    useConnectToWebSocket(
        WebSocketEndpoint.SKYXPLORE_GAME_CONSTRUCTION_AREA,
        lastEvent => handleEvent(lastEvent),
        sendMessage => {
            const event = {
                eventName: WebSocketEventName.SKYXPLORE_GAME_CONSTRUCTION_AREA_OPENED,
                payload: constructionArea.constructionAreaId
            };
            sendMessage(JSON.stringify(event));
        }
    )

    const handleEvent = (lastEvent) => {
        if (!hasValue(lastEvent)) {
            return;
        }

        switch (lastEvent.eventName) {
            case WebSocketEventName.SKYXPLORE_GAME_CONSTRUCTION_AREA_MODIFIED:
                const payload = lastEvent.payload;
                if (hasValue(payload.buildingModules)) {
                    setBuildingModules(payload.buildingModules);
                }
                break;
        }
    }

    const getContent = () => {
        if (!hasValue(constructionAreaData)) {
            return;
        }

        return new MapStream(constructionAreaData.slots)
            .sorted((a, b) => buildingModuleCategoryLocalizationHandler.get(a.key).localeCompare(buildingModuleCategoryLocalizationHandler.get(b.key)))
            .map((buildingModulecategory, amount) => <ConstructionAreaSlots
                key={buildingModulecategory}
                openPage={openPage}
                buildingModuleCategory={buildingModulecategory}
                amount={amount}
                constructionArea={constructionArea}
                buildings={new Stream(buildingModules).filter(building => building.buildingModuleCategory === buildingModulecategory).toList()}
                setBuildings={setBuildingModules}
                setConfirmationDialogData={setConfirmationDialogData}
            />)
            .toList();
    }

    const openDeconstructConfirmation = () => {
        setConfirmationDialogData(new ConfirmationDialogData(
            "skyxplore-game-construction-area-deconstruction-confirmation",
            localizationHandler.get("deconstruct-construction-area-confirmation-title"),
            localizationHandler.get("deconstruct-construction-area-confirmation-detail", { name: constructionAreaLocalizationHandler.get(constructionArea.dataId) }),
            [
                <Button
                    id="skyxplore-game-construction-area-deconstruction-confirm-button"
                    label={localizationHandler.get("deconstruct")}
                    onclick={deconstruct}
                />,
                <Button
                    id="skyxplore-game-construction-area-deconstruction-cancel-button"
                    label={localizationHandler.get("cancel")}
                    onclick={() => setConfirmationDialogData(null)}
                />
            ]
        ))
    }

    const deconstruct = async () => {
        await SKYXPLORE_PLANET_SURFACE_DECONSTRUCT_CONSTRUCTION_AREA.createRequest(null, { constructionAreaId: constructionArea.constructionAreaId })
            .send();

        setConfirmationDialogData(null);
        closePage();
    }

    return (
        <div>
            <header id="skyxplore-game-construction-area-header">
                <h1>{localizationHandler.get("title", { constructionArea: constructionAreaLocalizationHandler.get(constructionArea.dataId) })}</h1>

                <Button
                    id="skyxplore-game-construction-area-close-button"
                    className="skyxplore-game-window-close-button"
                    label="X"
                    onclick={closePage}
                />
            </header>

            <main id="skyxplore-game-construction-area">
                <div id="skyxplore-game-construction-area-slots">
                    {getContent()}
                </div>
                <div id="skyxplore-game-construction-area-deconstruct-button-wrapper">
                    <Button
                        id="skyxplore-game-construction-area-deconstruct-button"
                        label={localizationHandler.get("deconstruct-construction-area", { name: constructionAreaLocalizationHandler.get(constructionArea.dataId) })}
                        onclick={openDeconstructConfirmation}
                    />
                </div>
            </main>

            {footer}
        </div>
    );
}

export default ConstructionArea;