import React, { useEffect, useState } from "react";
import Stream from "../../../../common/js/collection/Stream";
import NavigationHistoryItem from "./NavigationHistoryItem";
import PageName from "./PageName";
import Utils from "../../../../common/js/Utils";
import Map from "./map/Map";
import SolarSystem from "./solar_system/SolarSystem";
import Planet from "./planet/Planet";

const Navigation = ({ footer }) => {
    const [history, setHistory] = useState(sessionStorage.skyXplorePageHistory ? JSON.parse(sessionStorage.skyXplorePageHistory) : []);
    useEffect(() => { sessionStorage.skyXplorePageHistory = JSON.stringify(history) }, [history]);

    const openPage = (page) => {
        Utils.addAndSet(history, page, setHistory);
    }

    const closePage = () => {
        const lastPage = new Stream(history)
            .last()
            .orElseThrow("IllegalState", "History is empty.");

        Utils.removeAndSet(history, (item) => item === lastPage, setHistory);
    }

    const lastPage = new Stream(history)
        .last()
        .orElse(new NavigationHistoryItem(PageName.MAP));

    switch (lastPage.pageName) {
        case PageName.MAP:
            return <Map
                footer={footer}
                openPage={openPage}
            />
        case PageName.SOLAR_SYSTEM:
            return <SolarSystem
                solarSystemId={lastPage.data}
                footer={footer}
                closePage={closePage}
                openPage={openPage}
            />
        case PageName.PLANET:
            return <Planet
                footer={footer}
                planetId={lastPage.data}
                closePage={closePage}
                openPage={openPage}
            />
        default:
            Utils.throwException("IllegalArgument", "Unhandled PageName: " + lastPage.pageName);
    }
}

export default Navigation;