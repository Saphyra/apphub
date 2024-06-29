import React, { useState } from "react";
import "./planet_overview.css";
import StorageOverview from "./storage/StorageOverview";
import PopulationOverview from "./population/PopulationOverview";
import BuildingOverview from "./building/BuildingOverview";
import PriorityOverview from "./priority/PriorityOverview";
import useLoadSetting, { SettingType } from "../../../common/hook/Setting";
import Endpoints from "../../../../../../common/js/dao/dao";
import Utils from "../../../../../../common/js/Utils";
import Button from "../../../../../../common/component/input/Button";
import localizationData from "./planet_overview_localization.json";
import LocalizationHandler from "../../../../../../common/js/LocalizationHandler";

const PlanetOverview = ({
    planetId,
    storage,
    population,
    buildings,
    planetSize,
    priorities,
    setPriorities,
    openPage
}) => {
    const localizationHandler = new LocalizationHandler(localizationData);

    const [tabSettings, setTabSettings] = useState(
        {
            tabs: {
                storageOverview: true,
                population: true
            },
            storage: {},
            building: {}
        }
    );

    const updateTabSettings = (settings) => {
        setTabSettings(settings);

        saveTabSettings(settings, planetId);
    }

    const saveTabSettings = (settings, location) => {
        const payload = {
            location: location,
            type: SettingType.PLANET_OVERVIEW_TAB,
            data: settings
        };

        Endpoints.SKYXPLORE_DATA_CREATE_SETTING.createRequest(payload)
            .send();
    }

    useLoadSetting(
        SettingType.PLANET_OVERVIEW_TAB,
        planetId,
        (setting) => {
            if (Utils.hasValue(setting)) {
                setTabSettings(setting.data);
            }
        }
    );

    return (
        <div id="skyxplore-game-planet-overview">
            <StorageOverview
                storage={storage}
                planetId={planetId}
                openPage={openPage}
                tabSettings={tabSettings}
                updateTabSettings={updateTabSettings}
            />

            <PopulationOverview
                population={population.population}
                capacity={population.capacity}
                planetId={planetId}
                openPage={openPage}
                tabSettings={tabSettings}
                updateTabSettings={updateTabSettings}
            />

            <BuildingOverview
                buildings={buildings}
                planetSize={planetSize}
                tabSettings={tabSettings}
                updateTabSettings={updateTabSettings}
            />

            <PriorityOverview
                priorities={priorities}
                setPriorities={setPriorities}
                planetId={planetId}
                tabSettings={tabSettings}
                updateTabSettings={updateTabSettings}
            />

            <Button
                id="skyxplore-game-planet-overview-save-expands-button"
                label={localizationHandler.get("save-defaults")}
                onclick={() => saveTabSettings(tabSettings, null)}
            />
        </div>
    );
}

export default PlanetOverview;