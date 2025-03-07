import React, { useEffect, useState } from "react";
import Stream from "../../../../common/js/collection/Stream";
import NavigationHistoryItem from "./NavigationHistoryItem";
import PageName from "./PageName";
import Map from "./map/Map";
import SolarSystem from "./solar_system/SolarSystem";
import Planet from "./planet/Planet";
import "./navigation.css";
import ModifySurface from "./modify_surface/ModifySurface";
import Population from "./population/Population";
import Storage from "./storage/Storage";
import { addAndSet, removeAndSet, throwException } from "../../../../common/js/Utils";
import ConstructionArea from "./construction_area/ConstructionArea";
import ConstructBuildingModule from "./construct_building_module/ConstructBuildingModule";

const Navigation = ({ footer, setConfirmationDialogData }) => {
    const [history, setHistory] = useState(sessionStorage.skyXplorePageHistory ? JSON.parse(sessionStorage.skyXplorePageHistory) : []);
    useEffect(() => { sessionStorage.skyXplorePageHistory = JSON.stringify(history) }, [history]);

    const openPage = (page) => {
        addAndSet(history, page, setHistory);
    }

    const closePage = () => {
        const lastPage = new Stream(history)
            .last()
            .orElseThrow("IllegalState", "History is empty.");

        removeAndSet(history, (item) => item === lastPage, setHistory);
    }

    const lastPage = new Stream(history)
        .last()
        .orElse(new NavigationHistoryItem(PageName.MAP));

    const data = lastPage.data;

    switch (lastPage.pageName) {
        case PageName.MAP:
            return <Map
                footer={footer}
                openPage={openPage}
            />
        case PageName.SOLAR_SYSTEM:
            return <SolarSystem
                solarSystemId={data}
                footer={footer}
                closePage={closePage}
                openPage={openPage}
            />
        case PageName.PLANET:
            return <Planet
                footer={footer}
                planetId={data}
                closePage={closePage}
                openPage={openPage}
                setConfirmationDialogData={setConfirmationDialogData}
            />
        case PageName.MODIFY_SURFACE:
            return <ModifySurface
                closePage={closePage}
                footer={footer}
                planetId={data.planetId}
                surfaceId={data.surfaceId}
                surfaceType={data.surfaceType}
            />
        case PageName.POPULATION:
            return <Population
                closePage={closePage}
                footer={footer}
                planetId={data}
            />
        case PageName.STORAGE:
            return <Storage
                closePage={closePage}
                footer={footer}
                planetId={data}
                setConfirmationDialogData={setConfirmationDialogData}
            />
        case PageName.CONSTRUCTION_AREA:
            return <ConstructionArea
                openPage={openPage}
                closePage={closePage}
                footer={footer}
                constructionArea={data.constructionArea}
                setConfirmationDialogData={setConfirmationDialogData}
            />
        case PageName.CONSTRUCT_BUILDING_MODULE:
            return <ConstructBuildingModule
                closePage={closePage}
                footer={footer}
                constructionAreaId={data.constructionAreaId}
                buildingModuleCategory={data.buildingModuleCategory}
            />
        default:
            throwException("IllegalArgument", "Unhandled PageName: " + lastPage.pageName);
    }
}

export default Navigation;