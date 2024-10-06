import useLoader from "../../../../../common/hook/Loader";
import { SKYXPLORE_DATA_GET_SETTING } from "../../../../../common/js/dao/endpoints/skyxplore/SkyXploreDataEndpoints";

const useLoadSetting = (type, location, callback) => {
    const payload = {
        type: type,
        location: location
    }

    useLoader(
        SKYXPLORE_DATA_GET_SETTING.createRequest(payload),
        response => callback(response.value)
    );
}

export const SettingType = {
    POPULATION_ORDER: "POPULATION_ORDER",
    POPULATION_HIDE: "POPULATION_HIDE",
    PLANET_OVERVIEW_TAB: "PLANET_OVERVIEW_TAB",
}

export default useLoadSetting;