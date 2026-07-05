import useLoader from "common/hook/Loader";
import { SKYXPLORE_DATA_GET_SETTING } from "../../SkyXploreGameEndpoints";

const useLoadSetting = (type, location, callback, setDisplaySpinner) => {
    const payload = {
        type: type,
        location: location
    }

    useLoader({
        request: SKYXPLORE_DATA_GET_SETTING.createRequest(payload),
        mapper: response => callback(response.value),
        setDisplaySpinner: setDisplaySpinner
    });
}

export const SettingType = {
    POPULATION_ORDER: "POPULATION_ORDER",
    POPULATION_HIDE: "POPULATION_HIDE",
    PLANET_OVERVIEW_TAB: "PLANET_OVERVIEW_TAB",
}

export default useLoadSetting;